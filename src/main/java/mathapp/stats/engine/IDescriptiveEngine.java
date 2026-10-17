package mathapp.stats.engine;

import mathapp.stats.dto.DescriptiveStatsResult;
import java.util.List;

public interface IDescriptiveEngine {
    DescriptiveStatsResult computeDescriptiveStats(List<Double> data, boolean isSample, double binWidth);
    DescriptiveStatsResult computeGroupedStats(List<Double> midpoints, List<Double> frequencies, boolean isSample);
}