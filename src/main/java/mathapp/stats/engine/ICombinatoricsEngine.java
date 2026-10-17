package mathapp.stats.engine;

import mathapp.stats.dto.CombinatoricsResult;

public interface ICombinatoricsEngine {
    CombinatoricsResult computeCombinatorics(int n, int r);
}