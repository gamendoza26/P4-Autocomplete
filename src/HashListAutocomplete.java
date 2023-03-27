import java.util.*;

public class HashListAutocomplete implements Autocompletor {

    private static final int MAX_PREFIX = 10;
    private Map<String, List<Term>> myMap;
    private int mySize;

    public HashListAutocomplete(String[] terms, double[] weights) {
        if (terms == null || weights == null) {
			throw new NullPointerException("One or more arguments null");
		}

		if (terms.length != weights.length) {
			throw new IllegalArgumentException("terms and weights are not the same length");
		}
		initialize(terms,weights);
    }

    @Override
    public List<Term> topMatches(String prefix, int k) {
        if (prefix.length() >= MAX_PREFIX) {
            prefix = prefix.substring(0,MAX_PREFIX);
        }
        if (myMap.containsKey(prefix)) {
            List<Term> all = myMap.get(prefix);
            List<Term> list = all.subList(0, Math.min(k, all.size()));
            return list;
        }
        return new ArrayList<>();
    }

    @Override
    public void initialize(String[] terms, double[] weights) {
        if (myMap != null) {
            myMap.clear();
        }
        myMap = new HashMap<>();
        List<Term> allTerms = new ArrayList<>();
        for(int i = 0; i < terms.length; i++) {
            allTerms.add(new Term(terms[i], weights[i]));
            mySize = mySize + terms[i].length() * BYTES_PER_CHAR;
            mySize = mySize + BYTES_PER_DOUBLE;
        }
        
        for (int i = 0; i < allTerms.size(); i++) {
            for (int k = 0; k < MAX_PREFIX && k <= allTerms.get(i).getWord().length(); k++) {
                String s = allTerms.get(i).getWord().substring(0, k);
                myMap.putIfAbsent(s, new ArrayList<Term>());
                myMap.get(s).add(allTerms.get(i));
            }
        }
        myMap.putIfAbsent("", allTerms);

        for (String s : myMap.keySet()) {
            mySize = mySize + s.length() * BYTES_PER_CHAR;
        }

        for (List<Term> L: myMap.values()) {
            Collections.sort(L, Comparator.comparing(Term::getWeight).reversed());
        }
    }

    @Override
    public int sizeInBytes() {
        // TODO Auto-generated method stub
        return mySize;
    }
    
}

