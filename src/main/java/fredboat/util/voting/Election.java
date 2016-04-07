package fredboat.util.voting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class Election {

    private final HashMap<Object, ArrayList<Object>> candidates;

    public Election(ArrayList<Object> cands) {
        candidates = new HashMap<>();

        for (Object candidate : cands) {
            candidates.put(candidate, new ArrayList<>());
        }
    }

    public Object setVote(Object voter, Object newCandidate) {
        Object originalCandidate = null;
        for (Object cand : candidates.keySet()) {
            ArrayList<Object> v = candidates.get(cand);
            if (v.contains(voter)) {
                originalCandidate = cand;
                v.remove(voter);
            }
        }

        candidates.get(newCandidate).add(voter);

        return originalCandidate;
    }

    public ArrayList<Object> getSortedByVotes() {
        ArrayList<Object> newList = new ArrayList<>(candidates.keySet());

        Collections.sort(newList, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                int s1 = candidates.get(o1).size();
                int s2 = candidates.get(o2).size();
                return Math.max(1, Math.min(-1, s1 - s2));
            }
        });

        return newList;
    }

}
