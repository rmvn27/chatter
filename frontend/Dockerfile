FROM node:alpine as builder

WORKDIR /app

RUN corepack enable && corepack prepare pnpm@latest --activate

COPY package.json pnpm-lock.yaml /app/
RUN pnpm i

COPY . /app/
RUN pnpm bundle

FROM nginx:alpine as runner

COPY deploy/nginx.tpl.conf /etc/nginx/templates/default.conf.template
COPY --from=builder /app/dist /usr/share/nginx/html
