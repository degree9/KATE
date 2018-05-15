(ns app.server
 (:require
  [meta.server :as svr]
  [kate.services :as kate]
  [degree9.services :as svc]))

(-> svr/app
    svr/with-defaults
    svr/with-rest
    svr/with-socketio
    svr/with-channels
    kate/tenant
    kate/namespace
    kate/deployment
    svc/entrypoint)

(defn- main []
 (svr/listen svr/app "8080"))

(defn init []
  (svr/init! main))
