package classifier;

abstract public class Solution {
    protected int rank;
    protected double crowdingDistance;

    public int getRank() {
        return this.rank;
    }

    public double getCrowdingDistance() {
        return this.crowdingDistance;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setCrowdingDistance(double crowdingDistance) {
        this.crowdingDistance = crowdingDistance;
    }

    public void addCrowdingDistance(double crowdingDistance) {
        this.crowdingDistance += crowdingDistance;
    }
}
