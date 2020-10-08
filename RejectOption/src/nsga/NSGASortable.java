package nsga;

public interface NSGASortable {
    public void setCrowdingDistance(double crowdingDistance);
    public void addCrowdingDistance(double crowdingDistance);
    public void setParetoRank(int paretoRank);
    public int getParetoRank();
    public double getCrowdingDistance();
}
