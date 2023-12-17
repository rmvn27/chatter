import { WebsocketService } from "@/services/websocketService";

export class LocationChangeTracker {
  private teamSlug: string | undefined = undefined;
  private channelSlug: string | undefined = undefined;
  constructor(private readonly wsService: WebsocketService) {}

  // send location events based on the previous states
  changeLocation = async ({
    teamSlug,
    channelSlug,
  }: {
    teamSlug?: string;
    channelSlug?: string;
  }) => {
    {
      // if we don't have any previous data
      // we determine the action solely by the new data
      //
      // this can happen when the user visits the site at the root
      // or navigates with a link to it
      if (this.teamSlug === undefined && this.channelSlug === undefined) {
        // see what values are defined. if both are defined send a combined event
        if (teamSlug !== undefined && channelSlug !== undefined) {
          this.wsService.send({ type: "enterTeamAndChannel", teamSlug, channelSlug });
        } else if (teamSlug !== undefined) {
          this.wsService.send({ type: "enterTeam", teamSlug });
        } else if (channelSlug !== undefined) {
          this.wsService.send({ type: "enterChannel", channelSlug });
        }
      } else {
        // theses cases can only happen when the user navigates inside the app
        // then realisticly he can only change one of them
        //
        // edge case: the user goes to their own settings and then
        // they remove themselves from a channel and team
        // because of this we have two seperate if statements
        if (channelSlug === undefined) {
          // first case: the channel is now undefined -> we left the channel
          this.wsService.send({ type: "leaveChannel" });
        } else if (this.channelSlug !== channelSlug) {
          // second case: the channel is different -> change channel
          this.wsService.send({ type: "enterChannel", channelSlug });
        }

        if (teamSlug === undefined) {
          // first case: the channel is now undefined -> we left the channel
          this.wsService.send({ type: "leaveTeam" });
        } else if (this.teamSlug !== teamSlug) {
          // second case: the channel is different -> change channel
          this.wsService.send({ type: "enterTeam", teamSlug });
        }
      }
      // now set the new incoming data as old
      this.teamSlug = teamSlug;
      this.channelSlug = channelSlug;
    }
  };
}
