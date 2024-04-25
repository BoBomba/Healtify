FROM node:20.11.1-alpine3.19

WORKDIR /app

ENV PATH /usr/src/app/node_modules/.bin:$PATH

COPY healtify/package.json ./
COPY healtify/package-lock.json ./
RUN npm install --silent
RUN npm install react-scripts@3.4.1 -g --silent

COPY healtify ./

CMD ["npm", "start"]