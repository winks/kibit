(ns jonase.kibit.reporters
  (:require [clojure.string :as string]
            [clojure.pprint :as pp])
  (:import [java.io StringWriter]))

;; A hack to get the code indented. 
(defn pprint-code [form]
  (let [string-writer (StringWriter.)]
    (pp/write form
              :dispatch pp/code-dispatch
              :stream string-writer
              :pretty true)
    (->> (str string-writer)
         string/split-lines
         (map #(str "  " %))
         (string/join "\n")
         println))) 

(defn splitter [form]
  (let [string-writer (StringWriter.)]
    (pp/write form
              :dispatch pp/code-dispatch
              :stream string-writer
              :pretty true)
    (->> (str string-writer)
         string/split-lines)))

(defn diff-lists [a b]
  (map #(str "" (string/trim %))
       (filter string?
               (map #(when-not (= %1 %2) %1) a b))))

(defn cli-reporter [check-map]
  (let [{:keys [line expr alt]} check-map]
    (do 
      (printf "[%s] Consider:\n" line)
      (pprint-code alt)
      (println "instead of:")
      (pprint-code expr)
      (newline))))

;; This reporter writes Cinder's XML format
(defn xml-reporter [check-map]
  (let [{:keys [line expr alt]} check-map
        expr-list (splitter expr)
        alt-list (splitter alt)]
    (do
      (printf "  <error line=\"%s\" column=\"0\" severity=\"warning\"" line)
      (print " message=\"")
      (print (diff-lists alt-list expr-list))
      (print "\" pattern=\"")
      (print (diff-lists expr-list alt-list))
      (print "\" />")
      (newline))))