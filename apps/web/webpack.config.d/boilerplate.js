if(config.devServer) {
  config.devServer.historyApiFallback = {
    rewrites: [
      { from: /.*jellyfin-wasm.wasm/, to: '/jellyfin-wasm.wasm' },
    ]
  }
}
