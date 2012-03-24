(ns leiningen.kibit_xml
  (:require [clojure.tools.namespace :as clj-ns]
            [clojure.java.io :as io]
            [jonase.kibit.core :as kibit]
            [jonase.kibit.reporters :as reporters]))

(defn- convert-to-path
  "Converts namespace representation to file name"
  [path]
  (str "src/" (clojure.string/replace (str path) "." "/") ".clj"))

(defn kibit_xml
  "Make kibit's findings available to Eclipse/CCW using the Cinder plugin"
  [project]
  (let [paths (or (:source-paths project) [(:source-path project)])
        source-files (mapcat #(-> % io/file clj-ns/find-clojure-sources-in-dir)
                             paths)]
    (do
      (println "<cruisecontrol>")
      (printf "<padawan>\n")
      (doseq [source-file source-files]
        (printf "<file name=\"%s\">\n"
                (convert-to-path
                  (or (second (clj-ns/read-file-ns-decl source-file)) source-file)))
        (kibit/check-file source-file :reporter reporters/xml-reporter)
        (printf "</file>")
        (newline)
        (flush))
      (println "</padawan>")
      (println "</cruisecontrol>"))))