# antanalyzer
Analyzes ant files and it's dependencies to find unneeded targets that can be removed.<br>
Reads an ant file and all ant files this first ant file references.
Collects all ant targets and their dependencies to other targets from those files.
categorizes targets as used or unused.
This is done by determining if any target is needed to execute a set of main targets.
Main targets can be given as a parameter.
If no main targets are give, the default target will be used.

# Parameters

```
usage: antalyzer [-af <arg>] [-at <arg>] [-h] [-paf] [-pt] [-put]
----------
-af,--ant-file <arg>       relative or absolute path to the main ant file. This parameter is not optional.
-at,--ant-targets <arg>    list of all targets used when executing ant as parameter. This list is used to
                           mark all targets that are needed to execute these targets as used. If omitted,
                           the default target of the main ant file is used.
-h,--help                  print this message
-paf,--print-ant-files     print ant file list. This parameter is optional. Default is disabled.
-pt,--print-tree           print target dependency tree. This parameter is optional. Default is disabled.
-put,--print-unused-targetsprint unused target. This parameter is optional. Default is disabled.
----------
```
```
-pt, --print-tree
Example

1/3
├── dist (build.xml)
│   └── compile (build.xml)
│       └── init (build.xml)
│           └── dist (second-build.xml)
│               └── compile (second-build.xml)
│                   └── init (second-build.xml)

2/3
├── clean (build.xml)

3/3
└── clean (second-build.xml)
```

```
-paf,--print-ant-files
Example

# ant files
#     Ant file name                        targets
  0   references/case_2_2/build.xml           5
  1   references/case_2_2/second-build.xml    5
  2   sum of all ant files                    8

```
```
-put,--print-unused-targets
Example

# unused targets
  1 clean defined at C:\data\github\antanalyzer\references\case_2_2\build.xml 34:32
  2 clean defined at C:\data\github\antanalyzer\references\case_2_2\second-build.xml 33:32

```