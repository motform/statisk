(ns motform.statisk.fs
  "Various utility functions that abstract over `java.io`.
  Inspired by Raynes/fs.
  
  As anyone who has worked with the JVM will tell you, we can't
  really change the current working directory as such, we just have a
  dynamic var that we cat unto whatever path we want to be working on.
  Is it elegant? Meh. Does it work? Yes. As long as you set `*cwd*`. "
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

;;;
;;; CWD
;;;

(def ^:dynamic
  ^{:doc "A str of the faux 'working directly' that we use we to do things relative to somewhere other than the classpath base."}
  *cwd* "")

(defn- in-cwd
  "Return path relative to `*cwd*`, if set."
  [path]
  (str *cwd* path))

(defn- file-in-cwd
  "Return File relative to `*cwd*`, if set."
  [path]
  (io/file (in-cwd path)))

;;;
;;; Util
;;;

(defn only-path
  "Return only the path resulting from `path` and `slug`.
   foo/bar/baz[.hmtl] => foo/bar"
  [path]
  (let [file-path (in-cwd path)]
    (subs file-path 0 (str/last-index-of file-path "/"))))

(defn last-in-path
  [path]
  (-> path (str/split #"/") last))

(defn html-filename
  ([slug] (str slug ".html"))
  ([path slug] (str path "/" slug ".html")))

(defn file-name [path]
  (last (str/split path #"/")))

(defn resource-filename [path slug]
  (str path "/" slug))

(defn tree
  "Return vector representation of everything branching from path,
  similar to the Unix command tree. Useful for testing."
  [path]
  (mapv (fn [f]
          (let [full-path (str path "/" f)]
            (if (.isDirectory (file-in-cwd full-path))
              [f (tree full-path)]
              f)))
        (.list (file-in-cwd path))))

;;;
;;; Predicates
;;;

(defn path-exists?
  "Return true if path exists."
  [path]
  (.isDirectory (file-in-cwd path)))

;;;
;;; Writing functions
;;;

(defn- delete-dir!
  "Expects a java.io.File object, which it then deletes."
  [file]
  (when (.isDirectory file)
    (doseq [path (.listFiles file)]
      (delete-dir! path)))
  (io/delete-file file))

(defn empty-dir!
  "Expects a str file path. If *cwd* is set, path must be relative."
  [path]
  (let [file (file-in-cwd path)]
    (when (.isDirectory file) (delete-dir! file))
    (.mkdir file)))

(defn make-dir!
  "Write dir at `path`, throws exception of intermediary dirs are not present."
  [path]
  (.mkdir (file-in-cwd path)))

(defn make-dirs!
  "Write dirs at `path`."
  [path]
  (.mkdirs (file-in-cwd path)))

(defn- write-intermediary-dirs!
  "Write any missing intermediary dirs in `path`."
  [path]
  (let [file-path (file-in-cwd (only-path path))]
    (when-not (path-exists? file-path) ; could this be made into an when-let?
      (make-dirs! file-path))))

(defn copy-file!
  "Copy file at `path` to `dest`, creating intermediary directories as necessary.
  Throws exception when path is not a valid file."
  [path dest]
  (write-intermediary-dirs! dest)
  (io/copy (file-in-cwd path) (file-in-cwd dest)))

(defn copy-dir!
  "Copy directory at `path` to dest, creating intermediary directories as necessary."
  [path dest]
  (write-intermediary-dirs! dest)
  (make-dir! dest)
  (doseq [file-path (.listFiles (file-in-cwd path))]
    (let [target (str dest "/" (last-in-path (str file-path)))] 
      (if (.isDirectory (file-in-cwd file-path))
        (copy-dir!  file-path target)
        (copy-file! file-path target)))))

(defn write-file!
  "Write `content` to file at `path`, creating intermediary
  directories as necessary."
  [path content]
  (write-intermediary-dirs! path)
  (spit path content))

(comment
  (tree "resources/out")
  (empty-dir! "resources/out")
  (write-file! "foo/bar/baz.html" "dingo")
  (copy-file! "resources/css/styles.css" "dingo/rama.css")

  (ns-publics (ns-name 'clojure.java.io))
  )
