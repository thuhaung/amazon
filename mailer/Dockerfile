FROM node:14

WORKDIR /mailer
COPY package.json .
RUN npm install
COPY . .
CMD npm start