(ns org.motform.statisk.push
  "Pushing functions for use under `:site/push` during the first phase
  of`core/build-site!`. Each push is executed 'endpoint', meaning
  that it should not return the site map."
  (:require [org.motform.statisk.fs :as fs]))

(defn write-pages!
  "NOTE: Expects the target directory to be conflict free."
  [{:site/keys [pages path]}]
  (doseq [{:page/keys [slug html]} pages]
    (fs/write-file! (fs/html-filename path slug) html)))

(defn write-resources!
  "There are two kinds of resources, those we copy from a file and
  those we create pragmatically through some function. This is inferred
  implicitly by the presence of ether the `:resource/renderer` or `:resource/file` key."
  [{:site/keys [resources path]}] 
  (doseq [{:resource/keys [slug renderer]} resources]
    (when renderer
      (fs/write-file! (fs/resource-filename path slug) (renderer)))))

(defn copy-resources!
  "Copies a folder to `path`."
  [{:site/keys [resources path]}]
  (doseq [{:resource/keys [slug file dir]} resources]
    (cond
      file (fs/copy-file! file (fs/resource-filename path slug))
      dir  (fs/copy-dir!  dir  (str path "/" slug)))))
