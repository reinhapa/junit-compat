package net.reini.junit;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Simple implementation of a {@link ExecutorService} executing all tasks in the caller thread.
 *
 * @author Patrick Reinhart
 */
public final class SameThreadExecutorService extends AbstractExecutorService {
  private boolean shutdown;

  /**
   * Creates new executor service instance.
   * 
   * @return the newly created instance
   */
  public static ExecutorService create() {
    return new SameThreadExecutorService();
  }

  private SameThreadExecutorService() {}

  @Override
  public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
    return true;
  }

  @Override
  public boolean isShutdown() {
    return shutdown;
  }

  @Override
  public boolean isTerminated() {
    return shutdown;
  }

  @Override
  public void shutdown() {
    shutdown = true;
  }

  @Override
  public List<Runnable> shutdownNow() {
    return Collections.emptyList();
  }

  @Override
  public void execute(Runnable command) {
    command.run();
  }
}