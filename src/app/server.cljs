(ns app.server
 (:require
  [meta.server :as svr]
  [app.services :as app]
  [degree9.services :as svc]))

(-> svr/app
    svr/with-defaults
    svr/with-rest
    svr/with-socketio
    svr/with-channels
    app/namespace
    app/deployment
    svc/entrypoint)

(defn- main []
 (svr/listen svr/app "8080"))

(defn init []
  (svr/init! main))
