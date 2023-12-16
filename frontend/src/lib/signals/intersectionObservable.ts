import { Accessor, onCleanup, onMount } from "solid-js";

// expose a hook for handling the IntersecionObserable api
//
// the callback is called after each change of the intersecion observable
// meaning the state of the element has changed (is show now or got hidden)
export const useIntersecionObservable = (
  element: Accessor<Element | undefined>,
  action: (isIntersecting: boolean) => void,
) => {
  onMount(() => {
    const elem = element();
    if (elem == undefined) return;

    const observer = new IntersectionObserver((entries) => {
      action(entries[0]?.isIntersecting ?? false);
    });

    observer.observe(elem);

    onCleanup(() => observer.disconnect());
  });
};
