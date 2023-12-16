import type { Element } from "hast";
import rehypePrismPlus from "rehype-prism-plus/common";
import remarkParse from "remark-parse";
import remarkRehype from "remark-rehype";
import { For, createMemo, type Component } from "solid-js";
import { unified } from "unified";
import { MarkdownElement } from "./MarkdownElement";

// Only import when this module is imported
// Makes the initial load faster
import "./MarkdownContent.css";

type Props = {
  content: string;
};

// Processing steps: raw text -> md-ast -> html-ast -> html-ast (+syntax hightlight)
//
// The html-ast can be used to dyanmically create jsx elements
const mdProcessor = unified().use(remarkParse).use(remarkRehype).use(rehypePrismPlus);

// Insipred by: https://github.com/syntax-tree/hast-util-to-jsx-runtime
//
// The problem was that this library didn't work well with solid
// So we just recursively go over the tree and render dynamic nodes
// This should also preserve reactivity
export const MarkdownContent: Component<Props> = (props) => {
  // Run sync since we use no async transforms
  const root = createMemo(() => mdProcessor.runSync(mdProcessor.parse(props.content)));
  const elements = () =>
    root().children.filter((e) => e.type === "element") as Element[];

  return (
    <div class={"flex flex-col gap-0.5 text-zinc-300"}>
      <For each={elements()}>{(e) => <MarkdownElement element={e} />}</For>
    </div>
  );
};
