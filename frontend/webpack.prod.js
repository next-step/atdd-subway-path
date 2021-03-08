const path = require('path')
const OptimizeCssAssetsPlugin = require('optimize-css-assets-webpack-plugin')
const TerserPlugin = require('terser-webpack-plugin')

const outputPath = path.resolve(__dirname, '../src/main/resources/static')

module.exports = {
  mode: 'production',
  output: {
    path: outputPath,
    filename: '[name].js'
  },
  optimization: {
    splitChunks: {
      chunks: 'all',
      cacheGroups: {
        vendors: {
          test: /[\\/]node_modules[\\/]/,
          name: 'js/vendors'
        }
      }
    },
    minimizer: [
      new TerserPlugin({
        cache: true,
        parallel: true,
        terserOptions: {
          warnings: false,
          compress: {
            warnings: false,
            unused: true
          },
          ecma: 6,
          mangle: true,
          unused: true
        },
        sourceMap: true
      }),
      new OptimizeCssAssetsPlugin()
    ]
  }
}
