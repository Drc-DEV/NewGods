package pro.dracarys.newgods.data;

public class Sacrifice {

    private int number;
    private int reward;

    public Sacrifice(int number, int reward) {
        this.number = number;
        this.reward = reward;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }
}
