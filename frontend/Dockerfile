# syntax=docker/dockerfile:1

ARG NODE_VERSION=22.17.0

FROM node:${NODE_VERSION}-alpine

# Use development node environment for Angular dev server
ENV NODE_ENV development

WORKDIR /usr/src/app

# Install Angular CLI globally
RUN npm install -g @angular/cli

# Download dependencies as a separate step to take advantage of Docker's caching.
# Include dev dependencies since we need them for Angular development server
RUN --mount=type=bind,source=package.json,target=package.json \
    --mount=type=bind,source=package-lock.json,target=package-lock.json \
    --mount=type=cache,target=/root/.npm \
    npm ci

# Copy the rest of the source files into the image.
COPY . .

# Run the application as a non-root user.
USER node

# Copy the rest of the source files into the image.
COPY --chown=node:node . .

# Expose the port that the application listens on.
EXPOSE 4200

# Run the application.
CMD ["ng", "serve", "--host", "0.0.0.0", "--port", "4200"]
