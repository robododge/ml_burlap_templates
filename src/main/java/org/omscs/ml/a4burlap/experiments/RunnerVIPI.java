package org.omscs.ml.a4burlap.experiments;

import burlap.behavior.valuefunction.ValueFunction;

public interface RunnerVIPI extends Runner{

  void runAndSave(boolean visualize);
  void runAndSaveMulti(int episodes);

  void incrementEpisode();
}
