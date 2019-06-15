// Generated by the protoc-spring-rest compiler plugin.  DO NOT EDIT!
// source: google/protobuf/timestamp.proto
package com.google.protobuf;

import java.util.Map;
import java.util.List;
import com.google.gson.annotations.SerializedName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.ByteString;
import java.util.stream.Collectors;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import io.grpc.ManagedChannel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import io.grpc.StatusRuntimeException;
import org.springframework.http.HttpStatus;
import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@javax.annotation.Generated(
    value = "by protoc-spring-rest compiler plugin",
    comments = "Source: google/protobuf/timestamp.proto")
public class TimestampProtoREST {
  @ApiModel(
      value = "Timestamp",
      description =
          "A Timestamp represents a point in time independent of any time zone\n"
              + "or calendar, represented as seconds and fractions of seconds at\n"
              + "nanosecond resolution in UTC Epoch time. It is encoded using the\n"
              + "Proleptic Gregorian Calendar which extends the Gregorian calendar\n"
              + "backwards to year one. It is encoded assuming all minutes are 60\n"
              + "seconds long, i.e. leap seconds are \"smeared\" so that no leap second\n"
              + "table is needed for interpretation. Range is from\n"
              + "0001-01-01T00:00:00Z to 9999-12-31T23:59:59.999999999Z.\n"
              + "By restricting to that range, we ensure that we can convert to\n"
              + "and from  RFC 3339 date strings.\n"
              + "See [https://www.ietf.org/rfc/rfc3339.txt](https://www.ietf.org/rfc/rfc3339.txt).\n"
              + "# Examples\n"
              + "Example 1: Compute Timestamp from POSIX `time()`.\n"
              + "Timestamp timestamp;\n"
              + "timestamp.set_seconds(time(NULL));\n"
              + "timestamp.set_nanos(0);\n"
              + "Example 2: Compute Timestamp from POSIX `gettimeofday()`.\n"
              + "struct timeval tv;\n"
              + "gettimeofday(&tv, NULL);\n"
              + "Timestamp timestamp;\n"
              + "timestamp.set_seconds(tv.tv_sec);\n"
              + "timestamp.set_nanos(tv.tv_usec * 1000);\n"
              + "Example 3: Compute Timestamp from Win32 `GetSystemTimeAsFileTime()`.\n"
              + "FILETIME ft;\n"
              + "GetSystemTimeAsFileTime(&ft);\n"
              + "UINT64 ticks = (((UINT64)ft.dwHighDateTime) << 32) | ft.dwLowDateTime;\n"
              + "// A Windows tick is 100 nanoseconds. Windows epoch 1601-01-01T00:00:00Z\n"
              + "// is 11644473600 seconds before Unix epoch 1970-01-01T00:00:00Z.\n"
              + "Timestamp timestamp;\n"
              + "timestamp.set_seconds((INT64) ((ticks / 10000000) - 11644473600LL));\n"
              + "timestamp.set_nanos((INT32) ((ticks % 10000000) * 100));\n"
              + "Example 4: Compute Timestamp from Java `System.currentTimeMillis()`.\n"
              + "long millis = System.currentTimeMillis();\n"
              + "Timestamp timestamp = Timestamp.newBuilder().setSeconds(millis / 1000)\n"
              + ".setNanos((int) ((millis % 1000) * 1000000)).build();\n"
              + "Example 5: Compute Timestamp from current time in Python.\n"
              + "timestamp = Timestamp()\n"
              + "timestamp.GetCurrentTime()\n"
              + "# JSON Mapping\n"
              + "In JSON format, the Timestamp type is encoded as a string in the\n"
              + "[RFC 3339](https://www.ietf.org/rfc/rfc3339.txt) format. That is, the\n"
              + "format is \"{year}-{month}-{day}T{hour}:{min}:{sec}[.{frac_sec}]Z\"\n"
              + "where {year} is always expressed using four digits while {month}, {day},\n"
              + "{hour}, {min}, and {sec} are zero-padded to two digits each. The fractional\n"
              + "seconds, which can go up to 9 digits (i.e. up to 1 nanosecond resolution),\n"
              + "are optional. The \"Z\" suffix indicates the timezone (\"UTC\"); the timezone\n"
              + "is required. A proto3 JSON serializer should always use UTC (as indicated by\n"
              + "\"Z\") when printing the Timestamp type and a proto3 JSON parser should be\n"
              + "able to accept both UTC and other timezones (as indicated by an offset).\n"
              + "For example, \"2017-01-15T01:30:15.01Z\" encodes 15.01 seconds past\n"
              + "01:30 UTC on January 15, 2017.\n"
              + "In JavaScript, one can convert a Date object to this format using the\n"
              + "standard [toISOString()](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Date/toISOString]\n"
              + "method. In Python, a standard `datetime.datetime` object can be converted\n"
              + "to this format using [`strftime`](https://docs.python.org/2/library/time.html#time.strftime)\n"
              + "with the time format spec '%Y-%m-%dT%H:%M:%S.%fZ'. Likewise, in Java, one\n"
              + "can use the Joda Time's [`ISODateTimeFormat.dateTime()`](\n"
              + "http://www.joda.org/joda-time/apidocs/org/joda/time/format/ISODateTimeFormat.html#dateTime--\n"
              + ") to obtain a formatter capable of generating timestamps in this format.")
  public static class Timestamp {
    private Timestamp() {}

    @ApiModelProperty(
        required = false,
        name = "seconds",
        value =
            "Represents seconds of UTC time since Unix epoch\n"
                + "1970-01-01T00:00:00Z. Must be from 0001-01-01T00:00:00Z to\n"
                + "9999-12-31T23:59:59Z inclusive.")
    @SerializedName(value = "seconds")
    @JsonProperty(value = "seconds")
    public Long seconds_ = null;

    @ApiModelProperty(
        required = false,
        name = "nanos",
        value =
            "Non-negative fractions of a second at nanosecond resolution. Negative\n"
                + "second values with fractions must still have non-negative nanos values\n"
                + "that count forward in time. Must be from 0 to 999,999,999\n"
                + "inclusive.")
    @SerializedName(value = "nanos")
    @JsonProperty(value = "nanos")
    public Integer nanos_ = null;

    public com.google.protobuf.Timestamp toProto() {
      com.google.protobuf.Timestamp.Builder builder = com.google.protobuf.Timestamp.newBuilder();
      if (seconds_ != null) {
        builder.setSeconds(seconds_);
      }
      if (nanos_ != null) {
        builder.setNanos(nanos_);
      }
      return builder.build();
    }

    public static Timestamp fromProto(com.google.protobuf.Timestamp input) {
      Timestamp newMsg = new Timestamp();
      newMsg.seconds_ = input.getSeconds();
      newMsg.nanos_ = input.getNanos();
      return newMsg;
    }
  }
}
