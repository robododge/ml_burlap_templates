package org.omscs.ml.a4burlap.mdp;

import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.domain.singleagent.gridworld.GridWorldRewardFunction;
import burlap.domain.singleagent.gridworld.state.GridAgent;
import burlap.domain.singleagent.gridworld.state.GridLocation;
import burlap.domain.singleagent.gridworld.state.GridWorldState;
import burlap.mdp.auxiliary.common.SinglePFTF;
import burlap.mdp.core.Domain;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.oo.propositional.PropositionalFunction;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import org.omscs.ml.a4burlap.mdp.grid.Coordinates;
import org.omscs.ml.a4burlap.mdp.grid.GridMaps;
import org.omscs.ml.a4burlap.mdp.grid.Hazard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MDPGridWorld implements MDPDef {

  private double defaultReward;
  private double goalReward;
  private Coordinates start;
  private Set<Coordinates> goals;
  private int[][] matrix;
  private List<Hazard> hazards;
  private HashMap<Hazard.HazardType, Double> hazardRewards;

  private HashableStateFactory hashableStateFactory;

  private double transitionProbability;

  private ProblemSize problemSize;

  static final int DEFAULT_GOAL_REWARD = 10;
  static final int DEFAULT_ALL_REWARD = -1;
  static final double DEFAULT_TRANS_PROB = 0.8d;

  public MDPGridWorld(ProblemSize problemSize) {
    this(DEFAULT_ALL_REWARD, DEFAULT_GOAL_REWARD, DEFAULT_TRANS_PROB, makeDefaultHazardMap(), problemSize);
  }

  public MDPGridWorld(double defaultReward, double goalReward, double transitionProb, HashMap<Hazard.HazardType, Double> hazardRewards, ProblemSize problemSize) {
    this.defaultReward = defaultReward;
    this.goalReward = goalReward;
    this.hazardRewards = hazardRewards;
    this.problemSize = problemSize;
    this.transitionProbability = transitionProb;

    String[] rawGridMap = null;
    if (problemSize == ProblemSize.LARGE) {
      rawGridMap = GridMaps.GRID_MAP_SMALL;
    } else {
      rawGridMap = GridMaps.GRID_MAP_LARGE;
    }

    int rows = rawGridMap.length;
    int cols = rawGridMap[0].toCharArray().length;
    this.matrix = new int[rows][cols];

    this.goals = new HashSet<Coordinates>();
    this.hashableStateFactory = new SimpleHashableStateFactory();

    invertAndProcessGrid(rawGridMap);
  }

  @Override
  public State getInitialState() {

    List<GridLocation> gridLocations = new ArrayList<>();
    int i=0;
    for( Coordinates goal: this.goals) {
      gridLocations.add(new GridLocation(goal.x, goal.y, "mygoal-"+i ));
      i++;
    }
    //Tie the GridAgent's start point and the goals into the initial state.
    GridWorldState initialState = new GridWorldState(new GridAgent(this.start.x, this.start.y), gridLocations);
    return initialState;
  }

  @Override
  public HashableStateFactory getHashableStateFactory() {
    return this.hashableStateFactory;
  }

  @Override
  public void reset() {
    this.hashableStateFactory = new SimpleHashableStateFactory();
  }

  @Override
  public Domain generateDomain() {
    GridWorldDomain gridWorldDomain = new GridWorldDomain(this.getWidth(), this.getWidth());
    gridWorldDomain.setMap(this.matrix);
    gridWorldDomain.setProbSucceedTransitionDynamics(this.transitionProbability);

    TerminalFunction terminalFunction = new SinglePFTF(PropositionalFunction.findPF(gridWorldDomain.generatePfs(), GridWorldDomain.PF_AT_LOCATION));

    // This sets the reward default for all cells in grid world.
    GridWorldRewardFunction rewardFunction = new GridWorldRewardFunction(this.getWidth(), this.getWidth(), this.defaultReward);


    //This sets the reward for the cell representing the goal.
    for (Coordinates goal : this.goals ) {
      rewardFunction.setReward(goal.x, goal.y, this.goalReward);
    }

    // This sets up all the rewards associated with the different hazards specified on the
    for (Hazard hazard : this.hazards) {
      rewardFunction.setReward(hazard.getLocation().x, hazard.getLocation().y, hazard.getReward());
    }

    gridWorldDomain.setTf(terminalFunction);
    gridWorldDomain.setRf(rewardFunction);

    OOSADomain domain = gridWorldDomain.generateDomain();

    return (Domain)domain;
  }

  public int getWidth() {
    return matrix[0].length;
  }
  public int getHeight() {
    return matrix.length;
  }

  public static  HashMap<Hazard.HazardType, Double> makeDefaultHazardMap() {
    HashMap<Hazard.HazardType, Double> hazMap = new HashMap<Hazard.HazardType, Double>();
    hazMap.put(Hazard.HazardType.SMALL, -1.0);
    hazMap.put(Hazard.HazardType.MEDIUM, -2.0);
    hazMap.put(Hazard.HazardType.LARGE, -3.0);
    return hazMap;
  }

  private void invertAndProcessGrid(String[] gridMap) {
    for (int i = 0; i < gridMap.length; i++) {
      for (int j = 0; j < gridMap[i].length(); j++) {
        int x = j;
        int y = getWidth() - 1 - i;

        this.matrix[x][y] = 0;
        if (gridMap[i].charAt(j) == '1') {
          this.matrix[x][y] = 1;
        } else if (gridMap[i].charAt(j) == 'X') {
          this.start = new Coordinates(x, y);
        } else if (gridMap[i].charAt(j) == 'G') {
          this.goals.add( new Coordinates(x, y));
        } else if (this.hazardRewards != null) {
          if (gridMap[i].charAt(j) == 'S') {
            this.hazards.add(
                new Hazard(
                    x,
                    y,
                    this.hazardRewards.get(Hazard.HazardType.SMALL),
                    Hazard.HazardType.SMALL));
          } else if (gridMap[i].charAt(j) == 'M') {
            this.hazards.add(
                new Hazard(
                    x,
                    y,
                    this.hazardRewards.get(Hazard.HazardType.MEDIUM),
                    Hazard.HazardType.MEDIUM));
          } else if (gridMap[i].charAt(j) == 'L') {
            this.hazards.add(
                new Hazard(
                    x,
                    y,
                    this.hazardRewards.get(Hazard.HazardType.LARGE),
                    Hazard.HazardType.LARGE));
          }
        }
      }
    }
  }
}
