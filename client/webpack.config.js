const path = require("path");
const webpack = require("webpack");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const Dotenv = require("dotenv-webpack");

module.exports = (env) => {
  const isDevelopment = process.env.NODE_ENV !== "production";

  return {
    mode: isDevelopment ? "development" : "production",
    entry: path.resolve(__dirname, "src/index.tsx"),
    output: {
      publicPath: "/",
      path: path.resolve(__dirname, "public"),
      filename: "[name].js",
      clean: true,
      assetModuleFilename: "[name][ext]",
    },
    module: {
      rules: [
        {
          test: /\.(ts|tsx)$/,
          exclude: /node_modules/,
          resolve: {
            extensions: [".ts", ".tsx", ".js", ".json"],
          },
          use: "ts-loader",
        },
        {
          test: /\.scss$/,
          exclude: /node_modules/,
          use: ["style-loader", "css-loader", "sass-loader"],
        },
        {
          test: /\.(png|jpe?g|gif|svg|webp)$/,
          use: "file-loader",
        },
      ],
    },
    plugins: [
      new HtmlWebpackPlugin({
        filename: "index.html",
        template: path.resolve(__dirname, "src/index.html"),
        favicon: "src/assets/favicon.ico",
      }),
      new Dotenv({
        path: ".env"
      }),
      new webpack.DefinePlugin({
        "process.env.NODE_ENV": JSON.stringify("development")
      })
    ],
    devtool: isDevelopment ? "source-map" : undefined,
    devServer: {
      static: {
        directory: path.resolve(__dirname, "public"),
      },
      port: 3000,
      open: true,
      hot: true,
      compress: true,
      historyApiFallback: true,
    },
  };
};
