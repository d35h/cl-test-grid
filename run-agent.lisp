;;; -*- Mode: LISP; Syntax: COMMON-LISP; indent-tabs-mode: nil; coding: utf-8;  -*-
;;;
;;; Copyright (C) 2011 Anton Vodonosov (avodonosov@yandex.ru)
;;;
;;; See LICENSE for details.
;;;
;;; This file is supposed to be LOADed by user: 
;;;
;;;        (load "agent.lisp")
;;;

(let* ((this-file (load-time-value (or *load-truename* #.*compile-file-pathname*)))
       (this-file-dir (make-pathname :directory (pathname-directory this-file))))

  (pushnew this-file-dir asdf:*central-registry* :test #'equal))

(asdf:operate 'asdf:load-op :test-grid)

(let ((results-dir (test-grid::run-libtests)))
    (test-grid::submit-test-run results-dir))


