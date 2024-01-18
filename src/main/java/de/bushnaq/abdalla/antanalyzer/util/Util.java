package de.bushnaq.abdalla.antanalyzer.util;

public class Util {
    private static final int DAY_INDEX = 3;
    private static final int HOUR_INDEX = 2;
//    public static Locale locale = Locale.GERMANY;
//    private static final Logger logger = LoggerFactory.getLogger(XlsxUtil.class);
    private static final int MINUTE_INDEX = 1;
    private static final int SECONDS_INDEX = 0;
    private static final int WEEK_INDEX = 4;
    private static final int MILLI_SECONDS_INDEX = 0;


    /**
     * If aUseCharacters is true, seconds will be followed with s, hours with h...
     * Result will be xd xh:xm:xs or x x:x:x
     *
     * @param aTime
     * @param aUseSeconds
     * @param aUseCharacters
     * @param aPrintLeadingZeros
     * @return String
     */
    public static String createDurationString(long aTime, final boolean aUseSeconds, final boolean aUseCharacters, final boolean aPrintLeadingZeros) {
        String prefix;
        if (aTime < 0) {
            prefix = "-";
            aTime = -aTime;
        } else {
            prefix = "";
        }
        String _result = "";
        final long[] _timePieces = {0, 0, 0, 0, 0};
        final TimeStruct[] _time = {new TimeStruct(" ", "s", 2), new TimeStruct(" ", "m", 2), new TimeStruct(" ", "h", 2),
                new TimeStruct(" ", "d", 2), new TimeStruct(" ", "w", 3)};
        _timePieces[WEEK_INDEX] = aTime / (27000000L * 5L);// assuming 5 day working week
        aTime -= _timePieces[WEEK_INDEX] * (27000000L * 5L);// assuming 5 day working week
        _timePieces[DAY_INDEX] = aTime / 27000000L;// assuming 7.5h day
        aTime -= _timePieces[DAY_INDEX] * 27000000L;// assuming 7.5h day
        _timePieces[HOUR_INDEX] = aTime / 3600000L;
        aTime -= _timePieces[HOUR_INDEX] * 3600000L;
        _timePieces[MINUTE_INDEX] = aTime / 60000L;
        aTime -= _timePieces[MINUTE_INDEX] * 60000L;
        _timePieces[SECONDS_INDEX] = aTime / 1000L;
        boolean _weFoundTheFirstNonezeroValue = aPrintLeadingZeros;
        int _indexEnd = 0;
        if (!aUseSeconds) {
            _indexEnd = 1;
        }
        for (int _index = WEEK_INDEX; _index >= _indexEnd; _index--) {
            if ((_timePieces[_index] != 0) || _weFoundTheFirstNonezeroValue) {
                if (aUseCharacters) {
                    if (_timePieces[_index] != 0) {
                        _result += longToString(_timePieces[_index], _weFoundTheFirstNonezeroValue);
                        _result += _time[_index].character;

                        if (_index != _indexEnd) {
                            _result += _time[_index].seperator;
                        } else {
                            // ---Do not add a seperator at the end
                        }
                    }

                } else {
                    _result += longToString(_timePieces[_index], _weFoundTheFirstNonezeroValue);
                    if (_index != _indexEnd) {
                        _result += _time[_index].seperator;
                    } else {
                        // ---Do not add a seperator at the end
                    }
                }
                _weFoundTheFirstNonezeroValue = true;
            } else {
                // ---Ignore all leading zero values
            }
        }
        // ---In case the result is empty
        if (_result.length() == 0) {
            if (aUseCharacters) {
                if (aUseSeconds) {
                    _result = "0s";
                } else {
                    _result = "0m";
                }
            } else {
                _result = "0";
            }
        } else {
            // ---The result is not ampty
        }
        return prefix + _result;
    }


    public static String longToString(final Long aValue, final boolean aCreateLeadingZero) {
        if (aValue != null) {
            if (!aCreateLeadingZero || (aValue > 9)) {
                return Long.toString(aValue);
            } else {
                return "0" + aValue;
            }
        } else {
            return "";
        }
    }
    /**
     * If aUseCharacters is true, seconds will be followed with s, hours with h... Result will be xd xh:xm:xs or x x:x:x
     *
     * @param aTime
     * @param aUseSeconds
     * @param aUseCharacters
     * @param aPrintLeadingZeros
     * @return String
     */
    public static String create24hDurationString(long aTime, final boolean aUseSeconds, final boolean useMilliSeconds, final boolean aUseCharacters,
                                                 final boolean aPrintLeadingZeros) {
        String prefix;
        if (aTime < 0) {
            prefix = "-";
            aTime = -aTime;
        }

        else {
            prefix = "";
        }
        String _result = "";
        final long[] _timePieces = { 0, 0, 0, 0, 0, 0 };
        final TimeStruct[] _time = { new TimeStruct(":", "ms", 3), new TimeStruct(" ", "s", 2), new TimeStruct(" ", "m", 2),
                new TimeStruct(" ", "h", 2), new TimeStruct(" ", "d", 2), new TimeStruct(" ", "w", 3) };
        _timePieces[WEEK_INDEX] = aTime / (86400000L * 7L);//assuming 5 day working week
        aTime -= _timePieces[WEEK_INDEX] * (86400000L * 7L);//assuming 5 day working week
        _timePieces[DAY_INDEX] = aTime / 86400000L;//assuming 7.5h day
        aTime -= _timePieces[DAY_INDEX] * 86400000L;//assuming 7.5h day
        _timePieces[HOUR_INDEX] = aTime / 3600000L;
        aTime -= _timePieces[HOUR_INDEX] * 3600000L;
        _timePieces[MINUTE_INDEX] = aTime / 60000L;
        aTime -= _timePieces[MINUTE_INDEX] * 60000L;
        _timePieces[SECONDS_INDEX] = aTime / 1000L;
        aTime -= _timePieces[SECONDS_INDEX] * 1000L;
        _timePieces[MILLI_SECONDS_INDEX] = aTime;

        boolean _weFoundTheFirstNonezeroValue = aPrintLeadingZeros;
        int _indexEnd = 0;
        if (!useMilliSeconds) {
            _indexEnd = 1;
        }
        if (!aUseSeconds) {
            _indexEnd = 2;
        }
        for (int _index = WEEK_INDEX; _index >= _indexEnd; _index--) {
            if ((_timePieces[_index] != 0) || _weFoundTheFirstNonezeroValue) {
                if (aUseCharacters) {
                    if (_timePieces[_index] != 0) {
                        _result += longToString(_timePieces[_index], _weFoundTheFirstNonezeroValue);
                        _result += _time[_index].character;
                    }

                    if (_index != _indexEnd) {
                        _result += _time[_index].seperator;
                    } else {
                        // ---Do not add a seperator at the end
                        if (_timePieces[_index] == 0) {
                            _result += "0";
                            _result += _time[_index].character;
                        }

                    }
                } else {
                    _result += longToString(_timePieces[_index], _weFoundTheFirstNonezeroValue);
                    if (_index != _indexEnd) {
                        _result += _time[_index].seperator;
                    } else {
                        // ---Do not add a seperator at the end
                    }
                }
                _weFoundTheFirstNonezeroValue = true;
            } else {
                // ---Ignore all leading zero values
            }
        }
        // ---In case the result is empty
        if (_result.length() == 0) {
            if (aUseCharacters) {
                if (aUseSeconds) {
                    _result = "0s";
                } else {
                    _result = "0m";
                }
            } else {
                _result = "0";
            }
        } else {
            // ---The result is not empty
        }
        return prefix + _result;
    }


}
