if(config.devServer) {
  config.devServer.historyApiFallback = {
    rewrites: [
      { from: /.*template-wasm.wasm/, to: '/template-wasm.wasm' },
    ]
  }
}
