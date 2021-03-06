import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Enrollee
{
    public final String id;
    public final int pointsSum;

    private Map<String, EnrolleeSpecData> specDataMap = new HashMap<>();

    public Enrollee(String id, int pointsSum)
    {
        this.id = id;
        this.pointsSum = pointsSum;
    }

    public void addSpec(String specKey, EnrolleeSpecData data)
    {
        specDataMap.put(specKey, data);
    }

    public void addSpec(String specKey, int index, boolean hasAgreement)
    {
        var data = new EnrolleeSpecData(index, hasAgreement);
        specDataMap.put(specKey, data);
    }

    public boolean hasSentTo(String spec)
    {
        return specDataMap.containsKey(spec);
    }

    @Nullable
    public String hasAgreementTo()
    {
        var keys = specDataMap.keySet();
        for (String key : keys)
        {
            if (specDataMap.get(key).hasAgreement)
            {
                return key;
            }
        }
        return null;
    }

    public int indexAt(String specKey)
    {
        return specDataMap.get(specKey).index;
    }

    public int compare(Enrollee other, String specKey)
    {
        return this.indexAt(specKey) - other.indexAt(specKey);
    }
}

class EnrolleeSpecData
{
    public final int index;
    public final boolean hasAgreement;

    public EnrolleeSpecData(int index, boolean hasAgreement)
    {
        this.index = index;
        this.hasAgreement = hasAgreement;
    }
}
