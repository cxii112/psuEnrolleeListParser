public class Enrollee
{
    public final int index;
    public final long id;
    public final boolean hasAgreement;
    public final int pointsSum;

    public Enrollee(int index, long id, boolean hasAgreement, int pointsSum)
    {
        this.index = index;
        this.id = id;
        this.hasAgreement = hasAgreement;
        this.pointsSum = pointsSum;
    }
}
