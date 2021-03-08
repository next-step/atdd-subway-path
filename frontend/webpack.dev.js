const path = require('path')

const outputPath = path.resolve(__dirname, 'out')

module.exports = {
  mode: 'development',
  devtool: 'cheap-eval-source-map',
  output: {
    path: outputPath,
    filename: '[name].js'
  },
  devServer: {
    contentBase: outputPath,
    publicPath: '/',
    host: '0.0.0.0',
    port: 8081,
    proxy: {
      '/resources/\\d*/js/(main|vendors).js': {
        target: 'http://127.0.0.1:8081',
        pathRewrite: {'/resources/\\d*' : ''}
      },
      '**': 'http://127.0.0.1:8080'
    },
    inline: true,
    hot: false
  }
}
