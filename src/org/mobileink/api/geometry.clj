(ns org.mobileink.api.geometry
  (:use [org.mobileink.api.utils])
  (:require [org.mobileink.api.impl default])
  (:require [org.mobileink.api.impl sequence])
  (:require [org.mobileink.api.multimethods :as gm])
  (:require [org.mobileink.api.protocols :as gp])
  (:require [org.mobileink.api.impl.pprint :as pprint]))

;; ==================================================================================
;; shamelessly stolen from core.matrix
;; https://github.com/mikera/core.matrix/blob/develop/src/main/clojure/clojure/core/matrix.clj
;;
;; api.geometry API namespace
;;
;; This is the public API for api.geometry
;;
;; General handling of operations is as follows:
;;
;; 1. user calls public AI function defined in clojure.core.matrix
;; 2. clojure.core.matrix function delegates to a protocol for the appropriate function
;;    with protocols as defined in the clojure.core.matrix.protocols namespace. In most cases
;;    clojure.core.matrix will try to delagate as quickly as possible to the implementation.
;; 3. The underlying matrix implementation implements the protocol to handle the API
;;    function call
;; 4. It's up to the implementation to decide what to do then
;; 5. If the implementation does not understand one or more parameters, then it is
;;    expected to call the multimethod version in clojure.core.matrix.multimethods as this
;;    will allow an alternative implementation to be found via multiple dispatch
;;
;; ==================================================================================

(def ^:dynamic *geometry-implementation* imp/DEFAULT-IMPLEMENTATION)

(defn point
  "Constructs a point from the given coordinates.

   If implementation is not specified, uses the current geometry library as specified
   in *geometry-implementation*"
  ([data]
     (or
      (gp/construct-matrix (implementation-check) data)
      (gp/coerce-param [] data)))
  ([implementation data]
     (or 
      (gp/construct-matrix (implementation-check implementation) data)
      (gp/coerce-param [] data))))

