#!/usr/bin/perl

# This file is part of SableCC ( http://sablecc.org ).
#
# See the NOTICE file distributed with this work for copyright information.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

use strict;
use File::Basename;

my %opts;

$opts{"d"} = "alt";

foreach my $file (@ARGV) {
        open my $in, "<", $file || die "$file: $!";
        my @lines = <$in>;
        close($file);

        my %alt;
        foreach my $l (@lines) {
                while ($l =~ /(\/\/(alt\d+))/g) {
                        $alt{$1} = $2;
                }
        }

        my @alt = sort(keys(%alt));
        foreach my $alt (@alt) {
                my ($name, $path, $suffix) = fileparse($file, ".sablecc");
                my $outfile = $name . "." . $alt{$alt} . $suffix;
                if (defined $opts{"d"}) {
                        $outfile = $opts{"d"} . "/" . $outfile;
                }
                print "$outfile\n";
                open my $out, ">", $outfile || die "$outfile: $!";
                foreach my $l (@lines) {
                        my $l2 = $l;
                        if ($l =~ /(\s*)(.*)(\s*)\Q$alt\E\b([ \t]*)(.*)(\s*)/) {
                                $l2 = "$1$5$3$alt$4$2$6";
                        }
                        print $out $l2;
                }
                close $out;
        }
}
