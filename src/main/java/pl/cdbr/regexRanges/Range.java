package pl.cdbr.regexRanges;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class Range {
    //potrzebne do sortowania
    private final static Comparator<Range> comparator = (r1, r2) -> Integer.compare(r1.from, r2.from);
    
    final int from, to;
    
    Range(int f, int t) {
        from = f;
        to = t;
    }
    
    List<Range> splitToPower(final int p) {
        final int pow = (int) Math.pow(10, p);
        int splitFrom = (int) (from / pow) * pow;
        int splitTo = splitFrom + pow - 1;
        final List<Range> spl = new ArrayList<>();
        while (splitFrom <= to) {
            Range r = new Range(from > splitFrom ? from : splitFrom, to < splitTo ? to : splitTo);
            spl.add(r);
            splitFrom += pow;
            splitTo += pow;
        }
        return spl;
    }
    
    boolean isFullInPower(final int p) {
        final int pow = (int) Math.pow(10, p);
        final int lowest = (int) (from / pow) * pow;
        final int highest = lowest + pow - 1;
        return (from == lowest && to == highest);
    }
    
    public String toString() {
        return String.format("Range[%d -> %d]", from, to);
    }
    
    Range joinRanges(final List<Range> rs) {
        rs.sort(comparator);
        return new Range(rs.get(0).from, rs.get(rs.size() - 1).to);
    }
    
    List<Range> split() {
        final int maxPower = (int) Math.log10(to);
        return split(maxPower);
    }
    
    List<Range> split(final int pow) {
        //jeżeli zeszliśmy do jedności (10^0), to już nie ma co dzielić
        if (pow == 0) {
            return Arrays.asList(this);
        }
        
        //podzielenie na pod-zakresy wg zadanej potęgi
        final Map<Boolean, List<Range>> partitioned = splitToPower(pow).stream()
        //podzielenie na full (true) i nie-full (false) - wynik ląduje w Map<Boolean, List<Range>>
            .collect(Collectors.partitioningBy(r -> r.isFullInPower(pow)));
        
        //nie-full (czyli początek i/lub koniec) lecą do dalszego przetwarzania
        final List<Range> notFull = partitioned.get(Boolean.FALSE);
        
        //każdy nie-full jest rekurencyjnie splitowany poziom niżej (o jedną potęgę 10 mniej)
        final List<Range> spl = notFull.stream()
            .flatMap( r -> r.split(pow - 1).stream())
            .collect(Collectors.toList());
        
        //dołączamy do listy zakresy full
        final List<Range> toJoin = partitioned.get(Boolean.TRUE);
        if (toJoin.size() > 0) {
            spl.add(joinRanges(toJoin));
        }
        //posortowane rosnąco
        spl.sort(comparator);
        
        return spl;
    }
    
    public String toSimpleRegex() {
        final String fromStr = Integer.toString(from);
        final String toStr = Integer.toString(to);
        if (fromStr.length() != toStr.length()) {
            //nie jest dobrze
            return "";
        }
        final StringBuilder rx = new StringBuilder();
        for (int i = 0; i < fromStr.length(); i++) {
            final char f = fromStr.charAt(i);
            final char t = toStr.charAt(i);
            if (f == t) {
                rx.append(f);
            } else {
                //"[f-t]"
                rx.append('[').append(f).append('-').append(t).append(']');
            }
        }
        return rx.toString();
    }
    
    public String toRegex() {
        final List<Range> rs = split();
        final String rx = rs.stream()
            .map(Range::toSimpleRegex)
            .collect(Collectors.joining("|"));
        return rx;
    }
    
    public static void main(String[] args) {
        final String rx = new Range(1000, 1954).toRegex();
        System.out.println(rx);
    }
}


