package nsga;

// There is a serious flaw in this design - the NSGASortable objects are reliant on some other process
// (the NSGA-2 sorting machine) to assign them values.  Additionally, there is not a good way to verify if these
// values are stale, so this is highly dependent on the programmer keeping everything straight (unlikely).
public interface NSGASortable {
    public void setCrowdingDistance(double crowdingDistance);
    public void addCrowdingDistance(double crowdingDistance);
    public void setParetoRank(int paretoRank);
    public int getParetoRank();
    public double getCrowdingDistance();
    public void setObjectives(double[] objectives);
}
