import type { Element, Text } from "hast";
import { For, Match, Switch, createMemo, type Component, type JSX } from "solid-js";
import { Dynamic } from "solid-js/web";

type Props = {
  element: Text | Element;
};

export const MarkdownElement: Component<Props> = (props) => {
  return (
    <Switch>
      <Match when={props.element.type === "text"}>
        {(props.element as Text).value}
      </Match>
      <Match when={props.element.type === "element"}>
        <TagElement element={props.element as Element} />
      </Match>
    </Switch>
  );
};

type TagElementProps = {
  element: Element;
};

const additionalClassesMap = {
  p: "text-md",
  // headings are essentially useless for chat messages
  // but still define their looks
  h1: "text-xl text-zinc-300 font-semibold pb-0.5",
  h2: "text-xl text-zinc-300 font-semibold",
  h3: "text-xl text-zinc-300 font-medium",
  h4: "text-lg text-zinc-300 font-medium",
  h5: "text-lg text-zinc-300 font-regular",
  h6: "text-lg text-zinc-300 font-regular",
  ul: "list-inside list-disc",
  ol: "list-inside list-decimal",
} as Record<keyof JSX.IntrinsicElements, string>;

// Create a dynamic element out of the ast element
//
// Join array properties into a string
// Also inject in the end own css classes to the elements
const TagElement: Component<TagElementProps> = (props) => {
  const tagName = () => props.element.tagName as keyof JSX.IntrinsicElements;
  const children = () => props.element.children as (Element | Text)[];
  const properties = createMemo(() => {
    const properties = props.element.properties ?? {};
    for (const [key, val] of Object.entries(properties)) {
      if (Array.isArray(val)) {
        properties[key] = val.join(" ");
      }
    }

    const additionalClasses = additionalClassesMap[tagName()];
    if (additionalClasses !== undefined) {
      const existingClasses = properties["className"];
      if (existingClasses === undefined) {
        properties["className"] = additionalClasses;
      } else {
        properties["className"] = `${existingClasses} ${additionalClasses}`;
      }
    }

    return properties;
  });

  return (
    <Dynamic component={tagName()} {...properties()}>
      <For each={children()}>{(e) => <MarkdownElement element={e} />}</For>
    </Dynamic>
  );
};
