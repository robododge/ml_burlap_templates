#OMSCS ML CS7641 Burlap Templates

## Setup and run
* This project uses java and gradle. Make sure you have a recent version of Java JDK installed (recommend JDK 15 or higher)
* Install gradle
  * Option 1: Gradle recommended [gradle.org install instrucitons](https://gradle.org/install/)
  * Option 2: use [ASDF](https://asdf-vm.com/guide/getting-started.html#_1-install-dependencies)
* Run gradle build `./gradlew build`

## Import into you IDE
* Intellij - import new gradle project, select the root directory of this project
* Eclipse - (no tested)

## Create and run your experiments.
A sample experiment has been provided in [RunExperiments.java](https://github.com/robododge/omscs_ml_a4_burlap/blob/main/src/main/java/org/omscs/ml/a4burlap/experiments/RunExperiments.java)
Edit this file to setup various experiment sizes, current examples:
1. Setup Large & Small GridWorldExperiments
2. Setup the Level1 & Level2 BlockDude experiments

Also, setup experiment MDP solver and learner alogorithms
3. Value Iteraion Experiments  (use the VISettings class to set hyperparametrs)
4. Policy Iteration Experiments (use the PISettings class to set hyperparametrs)
5. Q-Learning Experimnets (use the QSettings class to set hyperparametrs)

For running your experiments, you can just execute the main() of the RunExperiments.java class from your IDE.

## Experiment output

A CSV writer is attached to each experiment, the output filename of each experiment is controlled by a "shortName" 
which is configured as part of your experiment type settings, PISettings, VISettings or QSettings.  This short name
will provide a filename prefix for each of the experiment runs.

_Example file output_ `output/smprob-24105858/blockdude/`

**Metrics Captured:** Each experiment type has the ability to capture metrics collected during the iteraions of the experiments
here is sample of metrics collected:

1. "iter" - iteration id
2. "delta" - delta value found at each iteration
3. "wallclock" - wallclock time spent in each iteration, milliseconds for VI/PI, but nanosecond for QLearning
4. "evals" - the number of VI evals done within a single policy step for PI
5. "numSteps" - for QLearning, number of steps during last episode of learning
