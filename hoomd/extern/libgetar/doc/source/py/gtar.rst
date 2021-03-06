============================
Libgetar Python Module: gtar
============================

.. contents::
   :local:

Usage
=====

There are currently two main objects to work with in libgetar:
:py:class:`gtar.GTAR` archive wrappers and :py:class:`gtar.Record`
objects.

GTAR Objects
************

These wrap input and output to the zip file format and some minor
serialization and deserialization abilities.

.. autoclass:: gtar.GTAR
   :members:

When writing many small records at once, a :py:class:`gtar.BulkWriter`
object can be used.

.. autoclass:: gtar.BulkWriter
   :members:

Creation
--------

::

   # Open a trajectory archive for reading
   traj = gtar.GTAR('dump.zip', 'r')
   # Open a trajectory archive for writing, overwriting any dump.zip
   # in the current directory
   traj = gtar.GTAR('dump.zip', 'w')
   # Open a trajectory archive for appending, if you want to add
   # to the file without overwriting
   traj = gtar.GTAR('dump.zip', 'a')

Note that currently, due to a limitation in the miniz library we use,
you can't append to a zip file that's not using the zip64 format, such
as those generated by python's zipfile module in most cases (it only
makes zip64 if it has to for file size or count constraints; I didn't
see anything right off the bat to be able to force it to write in
zip64). See :ref:`Zip-vs-Zip64` below for solutions.

Simple API
----------

If you know the path you want to read from or store to, you can use
:py:func:`GTAR.readPath` and :py:func:`GTAR.writePath`:

::

    with gtar.GTAR('read.zip', 'r') as input_traj:
        props = input_traj.readPath('props.json')
        diameters = input_traj.readPath('diameter.f32.ind')

    with gtar.GTAR('write.zip', 'w') as output_traj:
        output_traj.writePath('oldProps.json', props)
        output_traj.writePath('mass.f32.ind', numpy.ones_like(diameters))

If you just want to read or write a string or bytestring, there are methods
:py:func:`GTAR.readStr`, :py:func:`GTAR.writeStr`,
:py:func:`GTAR.readBytes`, and :py:func:`GTAR.writeBytes`.

If you want to grab static properties by their name, there is
:py:func:`GTAR.staticRecordNamed`:

::

   diameters = traj.staticRecordNamed('diameter')

There are two methods that can be used to quickly get per-frame data for
time-varying quantities:

1. :py:func:`GTAR.framesWithRecordsNamed` is useful for "lazy" reading,
   because it returns the records and frame numbers which can be processed
   separately before actually reading data. This is especially helpful for
   retrieving every 100th frame of a file, for example. This is usually the
   most efficient way to retrieve data.

::

   (velocityRecord, frames) = traj.framesWithRecordsNamed('velocity')
   for frame in frames:
       velocity = traj.getRecord(velocityRecord, frame)
       kinetic_energy += 0.5*mass*numpy.sum(velocity**2)

   ((boxRecord, positionRecord), frames) = traj.framesWithRecordsNamed(['box', 'position'])
   good_frames = filter(lambda x: int(x) % 100 == 0, frames)
   for frame in good_frames:
       box = traj.getRecord(boxRecord, frame)
       position = traj.getRecord(positionRecord, frame)
       fbox = freud.box.Box(*box)
       rdf.compute(fbox, position, position)
       matplotlib.pyplot.plot(rdf.getR(), rdf.getRDF())

2. :py:func:`GTAR.recordsNamed`: is useful for iterating over **all** frames
   in the archive. It reads and returns the content of the records it finds.

::

   for (frame, vel) in traj.recordsNamed('velocity'):
       kinetic_energy += 0.5*mass*numpy.sum(vel**2)

   for (frame, (box, position)) in traj.recordsNamed(['box', 'position']):
       fbox = freud.box.Box(*box)
       rdf.compute(fbox, position, position)
       matplotlib.pyplot.plot(rdf.getR(), rdf.getRDF())

Advanced API
------------

The more complicated API can be used if you have multiple properties
with the same name (for example, a set of low-precision trajectories
for visualization and a less frequent set of dumps in double precision
for restart files).

Finding Available Records
~~~~~~~~~~~~~~~~~~~~~~~~~

A list of record types (records with blank indices) can be obtained by
the following:

::

   traj.getRecordTypes()

This can be filtered further in something like:

::

   positionRecord = [rec for rec in traj.getRecordTypes() if rec.getName() == 'position'][0]

The list of frames associated with a given record can be accessed as:

::

   frames = traj.queryFrames(rec)

Reading Binary Data
~~~~~~~~~~~~~~~~~~~

To read binary data (in the form of numpy arrays), use the following
method:

::

   traj.getRecord(query, index="")

This takes a :py:class:`gtar.Record` object specifying the path and an
optional index. Note that the index field of the record is nullified
in favor of the index passed into the method itself; usage might look
something like the following:

::

   positionRecord = [rec for rec in traj.getRecordTypes() if rec.getName() == 'position'][0]
   positionFrames = traj.queryFrames(positionRecord)
   positions = [traj.getRecord(positionRecord, frame) for frame in positionFrames]

Record Objects
**************

These objects are how you discover what is inside an archive and fetch
or store data. Records consist of several fields defining where in the
archive the data are stored, what type the data are, and so
forth. Probably the most straightforward way to construct one of these
yourself is to let the Record constructor itself parse a path within
an archive:

::

   rec = Record('frames/0/position.f32.ind')

.. autoclass:: gtar.Record
   :members:

Tools
=====

**gtar.fix**

.. automodule:: gtar.fix

**gtar.cat**

.. automodule:: gtar.cat

**gtar.copy**

.. automodule:: gtar.copy

**gtar.read**

.. automodule:: gtar.read

Enums: OpenMode, CompressMode, Behavior, Format, Resolution
===========================================================

.. autoclass:: gtar.OpenMode

.. autoclass:: gtar.CompressMode

.. autoclass:: gtar.Behavior

.. autoclass:: gtar.Format

.. autoclass:: gtar.Resolution
