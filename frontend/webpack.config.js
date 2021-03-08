const commonConfig = require('./webpack.common.js')
const webpackMerge = require('webpack-merge')
const { argv } = require('yargs')

module.exports = () => {
  const envConfig = require(`./webpack.${argv.env}.js`)
  return webpackMerge(commonConfig, envConfig)
}
