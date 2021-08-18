/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.9
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package libgetar_wrap;

public final class OpenMode {
  public final static OpenMode Read = new OpenMode("Read");
  public final static OpenMode Write = new OpenMode("Write");
  public final static OpenMode Append = new OpenMode("Append");

  public final int swigValue() {
    return swigValue;
  }

  public String toString() {
    return swigName;
  }

  public static OpenMode swigToEnum(int swigValue) {
    if (swigValue < swigValues.length && swigValue >= 0 && swigValues[swigValue].swigValue == swigValue)
      return swigValues[swigValue];
    for (int i = 0; i < swigValues.length; i++)
      if (swigValues[i].swigValue == swigValue)
        return swigValues[i];
    throw new IllegalArgumentException("No enum " + OpenMode.class + " with value " + swigValue);
  }

  private OpenMode(String swigName) {
    this.swigName = swigName;
    this.swigValue = swigNext++;
  }

  private OpenMode(String swigName, int swigValue) {
    this.swigName = swigName;
    this.swigValue = swigValue;
    swigNext = swigValue+1;
  }

  private OpenMode(String swigName, OpenMode swigEnum) {
    this.swigName = swigName;
    this.swigValue = swigEnum.swigValue;
    swigNext = this.swigValue+1;
  }

  private static OpenMode[] swigValues = { Read, Write, Append };
  private static int swigNext = 0;
  private final int swigValue;
  private final String swigName;
}
