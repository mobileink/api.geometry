(ns org.mobileink.api.geometry.impl.object-array
  (:require [org.mobileink.api.geometry.protocols :as gp])
  (:use org.mobileink.api.geometry.utils)
  (:require org.mobileink.api.geometry.impl.persistent-vector)
  (:require [org.mobileink.api.geometry.implementations :as imp])
  (:require [org.mobileink.api.geometry.impl.mathsops :as mops])
  (:require [org.mobileink.api.geometry.multimethods :as mm])
  (:import [java.util Arrays]))

;; taken
;; core.matrix/src/main/clojure/clojure/core/matrix/impl/object_array.clj
;; as example to modify for awt

(set! *warn-on-reflection* true)
(set! *unchecked-math* true)

;; org.mobileink.api.geometry implementation for Java awt

;; general arrays are represented as nested arrays wrapped by a Java Object[] array
;; in which case all sub-arrays must have the same shape.
;;
;; Useful as a fast, mutable implementation.

(def ^:const OBJECT-ARRAY-CLASS (Class/forName "[Ljava.lang.Object;"))

(defn construct-object-array ^objects [data]
  (let [dims (long (gp/dimensionality data))]
    (cond
      (== dims 1)
        (let [n (long (gp/dimension-count data 0))
              r (object-array n)]
           (dotimes [i n]
             (aset r i (gp/get-1d data i)))
           r)
      (== dims 0)
        (gp/get-0d data)
      :default
        (object-array (map construct-object-array (gp/get-major-slice-seq data))))))

(defn construct-nd ^objects [shape]
  (let [dims (long (count shape))] 
        (cond 
          (== 1 dims) (object-array (long (first shape)))
          (> dims 1)  
            (let [n (long (first shape))
                  m (object-array n)
                  ns (next shape)]
              (dotimes [i n]
                (aset m i (construct-nd ns)))
              m)
          :else (error "Can't make a nested object array of dimensionality: " dims))))

(defn object-array-coerce 
  "Coerce to object array format, avoids copying sub-arrays if possible."
  ([m]
  (if (> (gp/dimensionality m) 0) 
    (if (is-object-array? m)
      (let [^objects m m
            n (count m)]
        (loop [ret m i 0]
          (if (< i n)
            (let [mv (aget m i)
                  cmv (object-array-coerce mv)]
              (if (and (identical? m ret) (identical? mv cmv))
                (recur ret (inc i))
                (let [mm (copy-object-array m)]
                  (aset mm i cmv)
                  (recur mm (inc i)))))
            ret)))
      (object-array (map object-array-coerce (gp/get-major-slice-seq m))))
    (gp/get-0d m))))

(def ^Double ZERO 0.0)

(defmacro construct-object-vector [n]
  `(let [arr# (object-array ~n)]
     (Arrays/fill arr# ZERO)
     arr#))

(extend-protocol gp/PImplementation
  (Class/forName "[Ljava.lang.Object;")
    (implementation-key [m] :object-array)
    (meta-info [m]
      {:doc "org.mobileink.api.geometry implementation for Java Object arrays"})
    (new-vector [m length] (construct-object-vector (long length)))
    (new-matrix [m rows columns] 
      (let [columns (long columns)
            m (object-array rows)]
        (dotimes [i rows]
          (aset m i (construct-object-vector columns)))
        m))
    (new-matrix-nd [m shape]
      (construct-nd shape))
    (construct-matrix [m data]
      (construct-object-array data))
    (supports-dimensionality? [m dims]
      (>= dims 1)))


(extend-protocol gp/PDimensionInfo
  (Class/forName "[Ljava.lang.Object;")
    (dimensionality [m] 
      (let [^objects m m] 
        (+ 1 (gp/dimensionality (aget m 0)))))
    (is-vector? [m] 
      (let [^objects m m]
        (or 
         (== 0 (alength m))
         (== 0 (gp/dimensionality (aget m 0))))))
    (is-scalar? [m] false)
    (get-shape [m] 
      (let [^objects m m]
        (if (== 0 (alength m))
           1
           (cons (alength m) (gp/get-shape (aget m 0))))))
    (dimension-count [m x]
      (let [^objects m m
            x (long x)] 
        (cond 
          (== x 0)
            (alength m)
          (> x 0)
            (gp/dimension-count (aget m 0) (dec x))
          :else
            (error "Invalid dimension: " x)))))

;; explicitly specify we use the Object type
(extend-protocol gp/PTypeInfo
  (Class/forName "[Ljava.lang.Object;")
    (element-type [m]
      java.lang.Object))

(extend-protocol gp/PIndexedAccess
  (Class/forName "[Ljava.lang.Object;")
    (get-1d [m x]
      (aget ^objects m (int x)))
    (get-2d [m x y]
      (gp/get-1d (aget ^objects m (int x)) y))
    (get-nd [m indexes]
      (let [^objects m m
            dims (long (count indexes))]
        (cond
          (== 1 dims)
            (aget m (int (first indexes)))
          (> dims 1) 
            (gp/get-nd (aget m (int (first indexes))) (next indexes)) 
          (== 0 dims) m
          :else
            (error "Invalid dimensionality access with index: " (vec indexes))))))

(extend-protocol gp/PIndexedSetting
  (Class/forName "[Ljava.lang.Object;")
    (set-1d [m x v]
      (let [^objects arr (copy-object-array m)]
        (aset arr (int x) v)
        arr))
    (set-2d [m x y v]
      (let [^objects arr (copy-object-array m)
            x (int x)]
        (aset arr x (gp/set-1d (aget ^objects m x) y v))
        arr))
    (set-nd [m indexes v]
      (let [dims (long (count indexes))]
        (cond 
          (== 1 dims)
            (let [^objects arr (copy-object-array m)
                  x (int (first indexes))]
              (aset arr (int x) v)
              arr)
          (> dims 1)
            (let [^objects arr (copy-object-array m)
                  x (int (first indexes))]
              (aset arr x (gp/set-nd (aget ^objects m x) (next indexes) v))
              arr)  
          :else 
            (error "Can't set on object array with dimensionality: " (count indexes)))))
    (is-mutable? [m] true))

(extend-protocol gp/PIndexedSettingMutable
  (Class/forName "[Ljava.lang.Object;")
    (set-1d! [m x v]
      (aset ^objects m (int x) v))
    (set-2d! [m x y v]
      (gp/set-1d! (aget ^objects m x) y v))
    (set-nd! [m indexes v]
      (let [^objects m m
            dims (long (count indexes))]
        (cond 
          (== 1 dims)
            (aset m (int (first indexes)) v)
          (> dims 1)
            (gp/set-nd! (aget m (int (first indexes))) (next indexes) v)
          :else
            (error "Can't set on object array with dimensionality: " (count indexes))))))

(extend-protocol gp/PBroadcast
  (Class/forName "[Ljava.lang.Object;")
    (broadcast [m target-shape]
      (let [mshape (gp/get-shape m)
            dims (long (count mshape))
            tdims (long (count target-shape))]
        (cond
          (> dims tdims) 
            (error "Can't broadcast to a lower dimensional shape")
          (not (every? identity (map #(== %1 %2) mshape (take-last dims target-shape))))
            (error "Incompatible shapes, cannot broadcast " (vec mshape) " to " (vec target-shape))
          :else
            (reduce
              (fn [m dup] (object-array (repeat dup m)))
              m
              (reverse (drop-last dims target-shape)))))))

(extend-protocol gp/PCoercion
  (Class/forName "[Ljava.lang.Object;")
    (coerce-param [m param]
      (object-array-coerce param)))

(extend-protocol gp/PMutableMatrixConstruction
  (Class/forName "[Ljava.lang.Object;")
    (mutable-matrix [m]
      (if (> (gp/dimensionality m) 1)
        (object-array (map gp/mutable-matrix m))
        (object-array (map gp/get-0d m)))))

(extend-protocol gp/PConversion
  (Class/forName "[Ljava.lang.Object;")
    (convert-to-nested-vectors [m]
      (mapv gp/convert-to-nested-vectors (seq m))))

(extend-protocol gp/PMatrixSlices
  (Class/forName "[Ljava.lang.Object;")
    (get-row [m i]
      (gp/get-major-slice m i))
    (get-column [m i]
      (gp/get-slice m 1 i))
    (get-major-slice [m i]
      (aget ^objects m (long i)))
    (get-slice [m dimension i]
      (let [dimension (long dimension)]
        (if (== dimension 0)
          (gp/get-major-slice m i)
          (let [sd (dec dimension)]
            (mapv #(gp/get-slice % sd i) m))))))

(extend-protocol gp/PSliceView
  (Class/forName "[Ljava.lang.Object;")
    (get-major-slice-view [m i] 
      (let [^objects m m
            v (aget m i)]
        (if (gp/is-scalar? v)
          (org.mobileink.api.geometry.impl.wrappers/wrap-slice m i)
          v))))

(extend-protocol gp/PSliceSeq
  (Class/forName "[Ljava.lang.Object;")
    (get-major-slice-seq [m]
      (let [^objects m m]
        (if (and (> 0 (alength m)) (== 0 (gp/dimensionality (aget m 0)))) 
          (seq (map gp/get-0d m))
          (seq m)))))

(extend-protocol gp/PElementCount
  (Class/forName "[Ljava.lang.Object;")
    (element-count [m] 
      (let [^objects m m
            n (alength m)] 
        (if (== n 0)
          0
          (* n (gp/element-count (aget m 0)))))))

(extend-protocol gp/PValidateShape
  (Class/forName "[Ljava.lang.Object;")
    (validate-shape [m]
      (let [^objects m m
            shapes (map gp/validate-shape (seq m))]
        (if (gp/same-shapes? shapes)
          (cons (alength m) (first shapes))
          (error "Inconsistent shapes for sub arrays in object array"))))) 

(extend-protocol gp/PFunctionalOperations
  (Class/forName "[Ljava.lang.Object;")
    (element-seq [m]
      (let [^objects m m]
        (cond
          (== 0 (alength m)) 
            '()
          (> (gp/dimensionality (aget m 0)) 0)
            (mapcat gp/element-seq m)
          :else
            (map gp/get-0d m))))
    (element-map
      ([m f]
        (object-array (map #(gp/element-map % f) m)))
      ([m f a]
        (object-array (map #(gp/element-map %1 f %2) m (gp/get-major-slice-seq a))))
      ([m f a more]
        (object-array (apply map #(apply gp/element-map %1 f %2 %&) m (gp/get-major-slice-seq a) (map gp/get-major-slice-seq more)))))
    (element-map!
      ([m f]
        (dotimes [i (count m)] 
          (let [^objects m m
                s (aget m i)]
            (if (gp/is-mutable? s) 
              (gp/element-map! s f) 
              (aset m i (gp/element-map s f)))))
        m)
      ([m f a]
        (dotimes [i (count m)]
          (let [^objects m m
                s (aget m i)
                as (gp/get-major-slice a i)]
            (if (gp/is-mutable? s) 
              (gp/element-map! s f as)
              (aset m i (gp/element-map s f as)))))
        m)
      ([m f a more]
        (dotimes [i (count m)]
          (let [^objects m m
                s (aget m i)
                as (gp/get-major-slice a i)
                ms (map #(gp/get-major-slice % i) more)]
            (if (gp/is-mutable? s) 
              (apply gp/element-map! s f as ms)
              (aset m i (apply gp/element-map s f as ms)))))
        m))
    (element-reduce
      ([m f]
        (reduce f (gp/element-seq m)))
      ([m f init]
        (reduce f init (gp/element-seq m)))))


(imp/register-implementation (object-array [1]))
