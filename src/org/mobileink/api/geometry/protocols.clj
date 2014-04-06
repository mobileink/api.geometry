(ns org.mobileink.api.protocols
  ;; (:require [org.mobileink.api.utils :refer [error same-shape-object? broadcast-shape]])
  ;; (:require [org.mobileink.api.impl.mathsops :as mops]))
  )

(set! *warn-on-reflection* true)
(set! *unchecked-math* true)

;; ================================================================
;; based on
;; https://github.com/mikera/core.matrix/blob/develop/src/main/clojure/clojure/core/matrix/protocols.clj
;;
;; api.geometry API protocols
;;
;; Geometry implementations should extend these for full API support
;;
;; This namespace is intended for use by API implementers only
;; api.geometry users should not access these protocols directly
;;
;; ================================================================

;; ===================================================================================
;; MANDATORY PROTOCOLS FOR ALL IMPLEMENTATIONS
;;
;; A compliant api.geometry implementation must implement these.
;; Otherwise things will fail.

(defprotocol PImplementation
  "Protocol for general implementation functionality. Required to support implementation metadata and
   matrix construction."
  (implementation-key [m]
    "Returns a keyword representing this implementation.
     Each implementation should have one unique key.")
  (meta-info [m]
    "Returns meta-information on the implementation. It is expected that
     at least an element :doc containing a string describing an implementation
     is provided.")
  (construct-matrix [m data]
    "Returns a new matrix containing the given data. data should be in the form of either
     nested sequences or a valid existing matrix")
  (new-vector [m length]
    "Returns a new vector (1D column matrix) of the given length, filled with numeric zero.")
  (new-matrix [m rows columns]
    "Returns a new matrix (regular 2D matrix) with the given number of rows and columns, filled with numeric zero.")
  (new-matrix-nd [m shape]
    "Returns a new general matrix of the given shape.
     Must return nil if the shape is not supported by the implementation.
     Shape must be a sequence of dimension sizes.")
  (supports-dimensionality? [m dimensions]
    "Returns true if the implementation supports matrices with the given number of dimensions."))

