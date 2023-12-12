import { Component } from "solid-js";

export const ParticipantsSidebarHeading: Component = () => {
  const textClasses = "text-xl font-medium text-zinc-300 select-none";
  const borderClasses = "pb-1 border-b border-zinc-300/10";

  return <h2 class={`${textClasses} ${borderClasses}`}>Participants</h2>;
};
