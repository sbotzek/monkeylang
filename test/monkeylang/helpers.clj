(ns monkeylang.helpers
  (:require [clojure.test :refer :all]
            [clojure.data :as data]))

(defn is-no-diff
  [expected result]
  (let [[missing-from-result missing-from-expected in-both] (data/diff expected result)]
    (is (empty? missing-from-result))
    (is (empty? missing-from-expected))))
