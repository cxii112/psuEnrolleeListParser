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
}

class EnrolleeSpecData
{
    public final int index;
    public final boolean hasAgreement;

    public EnrolleeSpecData(int index, boolean hasAgreement)
    {
        this.index = 0;
        this.hasAgreement = hasAgreement;
    }
}
