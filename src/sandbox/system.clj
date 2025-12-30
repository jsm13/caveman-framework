(ns sandbox.system
  (:require
   [next.jdbc.connection :as connection]
   [proletarian.worker :as worker]
   [ring.adapter.jetty :as jetty]
   [ring.middleware.session.cookie :as session-cookie]
   [sandbox.jobs :as jobs]
   [sandbox.routes :as routes])
  (:import (com.zaxxer.hikari HikariDataSource)
           (io.github.cdimascio.dotenv Dotenv)
           (org.eclipse.jetty.server Server)))

(set! *warn-on-reflection* true)

(defn start-env
  []
  (Dotenv/load))

(defn start-cookie-store
  []
  (session-cookie/cookie-store))

(defn start-db
  [{::keys [env]}]
  (println "!!!!!!!!!!!!!!!!")
  (println (Dotenv/.get env "POSTGRES_HOST"))
  (connection/->pool HikariDataSource
                     {:dbtype "postgres"
                      :dbname "postgres"
                      :username (Dotenv/.get env "POSTGRES_USERNAME")
                      :password (Dotenv/.get env "POSTGRES_PASSWORD")
                      :host (Dotenv/.get env "POSTGRES_HOST")}))

(defn stop-db
  [db]
  (HikariDataSource/.close db))

(defn start-worker
  [{::keys [db] :as system}]
  (let [worker (worker/create-queue-worker
                db
                (partial #'jobs/process-job system)
                {:proletarian/log #'jobs/logger
                 :proletarian/serializer jobs/json-serializer})]
    (worker/start! worker)
    worker))

(defn stop-worker
  [worker]
  (worker/stop! worker))

(defn start-server
  [{::keys [env] :as system}]
  (println "Starting server")
  (let [handler (if (= (Dotenv/.get env "ENVIRONMENT")
                       "development")
                  (partial #'routes/root-handler system)
                  (routes/root-handler system))]
    (jetty/run-jetty 
     handler 
     {:port (Long/parseLong (Dotenv/.get env "PORT")) 
      :join? false})))

(defn stop-server
  [server]
  (Server/.stop server))

(defn start-system
  []
  (println "starting system")
  (let [system-so-far {::env (start-env)}
        system-so-far (merge system-so-far {::cookie-store (start-cookie-store)})
        system-so-far (merge system-so-far {::db (start-db system-so-far)})]
    (merge system-so-far {::server (start-server system-so-far)})))

(defn stop-system
  [system]
  (stop-server (::server system))
  (stop-worker (::worker system))
  (stop-db (::db system)))
