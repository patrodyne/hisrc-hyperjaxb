package org.jvnet.hyperjaxb.basicjaxb.lang;

import org.apache.commons.lang3.Validate;
import org.jvnet.hyperjaxb.jpa.Mergeable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jvnet.basicjaxb.lang.MergeStrategy;
import org.jvnet.basicjaxb.locator.ObjectLocator;

public class MergeableMergeStrategy implements MergeStrategy
{
	private Logger logger = LoggerFactory.getLogger(MergeStrategy.class);

	@Override
	public Logger getLogger()
	{
		return logger;
	}

	@Override
	public boolean isDebugEnabled()
	{
		return logger.isDebugEnabled();
	}

	@Override
	public boolean isTraceEnabled()
	{
		return logger.isTraceEnabled();
	}

	private MergeStrategy mergeStrategy;
	protected MergeStrategy getMergeStrategy() { return mergeStrategy; }
	protected void setMergeStrategy(MergeStrategy mergeStrategy) { this.mergeStrategy = mergeStrategy; }

	public MergeableMergeStrategy(MergeStrategy mergeStrategy)
	{
		Validate.notNull(mergeStrategy);
		setMergeStrategy(mergeStrategy);
	}

	@Override
	public Boolean shouldBeMergedAndSet(ObjectLocator leftLocator, ObjectLocator rightLocator,
		boolean leftSet, boolean rightSet)
	{
		return getMergeStrategy().shouldBeMergedAndSet(leftLocator, rightLocator, leftSet, rightSet);
	}

	@Override
	public boolean merge(ObjectLocator leftLocator, ObjectLocator rightLocator, boolean left, boolean right,
		boolean leftSet, boolean rightSet)
	{
		return getMergeStrategy().merge(leftLocator, rightLocator, left, right, leftSet, rightSet);
	}

	@Override
	public byte merge(ObjectLocator leftLocator, ObjectLocator rightLocator, byte left, byte right,
		boolean leftSet, boolean rightSet)
	{
		return getMergeStrategy().merge(leftLocator, rightLocator, left, right, leftSet, rightSet);
	}

	@Override
	public char merge(ObjectLocator leftLocator, ObjectLocator rightLocator, char left, char right,
		boolean leftSet, boolean rightSet)
	{
		return getMergeStrategy().merge(leftLocator, rightLocator, left, right, leftSet, rightSet);
	}

	@Override
	public double merge(ObjectLocator leftLocator, ObjectLocator rightLocator, double left, double right,
		boolean leftSet, boolean rightSet)
	{
		return getMergeStrategy().merge(leftLocator, rightLocator, left, right, leftSet, rightSet);
	}

	@Override
	public float merge(ObjectLocator leftLocator, ObjectLocator rightLocator, float left, float right,
		boolean leftSet, boolean rightSet)
	{
		return getMergeStrategy().merge(leftLocator, rightLocator, left, right, leftSet, rightSet);
	}

	@Override
	public int merge(ObjectLocator leftLocator, ObjectLocator rightLocator, int left, int right,
		boolean leftSet, boolean rightSet)
	{
		return getMergeStrategy().merge(leftLocator, rightLocator, left, right, leftSet, rightSet);
	}

	@Override
	public long merge(ObjectLocator leftLocator, ObjectLocator rightLocator, long left, long right,
		boolean leftSet, boolean rightSet)
	{
		return getMergeStrategy().merge(leftLocator, rightLocator, left, right, leftSet, rightSet);
	}

	@Override
	public short merge(ObjectLocator leftLocator, ObjectLocator rightLocator, short left, short right,
		boolean leftSet, boolean rightSet)
	{
		return getMergeStrategy().merge(leftLocator, rightLocator, left, right, leftSet, rightSet);
	}

	@Override
	public Object merge(ObjectLocator leftLocator, ObjectLocator rightLocator, Object left, Object right,
		boolean leftSet, boolean rightSet)
	{
		if (left instanceof Mergeable)
		{
			final Mergeable mergeable = (Mergeable) left;
			if (mergeable.isMerge())
				return getMergeStrategy().merge(leftLocator, rightLocator, left, right, leftSet, rightSet);
			else
			{
				// Do not merge (right uses null)
				return getMergeStrategy().merge(leftLocator, rightLocator, left, null, leftSet, rightSet);
			}
		}
		else
			return getMergeStrategy().merge(leftLocator, rightLocator, left, right, leftSet, rightSet);
	}

	@Override
	public boolean[] merge(ObjectLocator leftLocator, ObjectLocator rightLocator, boolean[] left, boolean[] right,
		boolean leftSet, boolean rightSet)
	{
		return getMergeStrategy().merge(leftLocator, rightLocator, left, right, leftSet, rightSet);
	}

	@Override
	public byte[] merge(ObjectLocator leftLocator, ObjectLocator rightLocator, byte[] left, byte[] right,
		boolean leftSet, boolean rightSet)
	{
		return getMergeStrategy().merge(leftLocator, rightLocator, left, right, leftSet, rightSet);
	}

	@Override
	public char[] merge(ObjectLocator leftLocator, ObjectLocator rightLocator, char[] left, char[] right,
		boolean leftSet, boolean rightSet)
	{
		return getMergeStrategy().merge(leftLocator, rightLocator, left, right, leftSet, rightSet);
	}

	@Override
	public double[] merge(ObjectLocator leftLocator, ObjectLocator rightLocator, double[] left, double[] right,
		boolean leftSet, boolean rightSet)
	{
		return getMergeStrategy().merge(leftLocator, rightLocator, left, right, leftSet, rightSet);
	}

	@Override
	public float[] merge(ObjectLocator leftLocator, ObjectLocator rightLocator, float[] left, float[] right,
		boolean leftSet, boolean rightSet)
	{
		return getMergeStrategy().merge(leftLocator, rightLocator, left, right, leftSet, rightSet);
	}

	@Override
	public int[] merge(ObjectLocator leftLocator, ObjectLocator rightLocator, int[] left, int[] right, boolean leftSet,
		boolean rightSet)
	{
		return getMergeStrategy().merge(leftLocator, rightLocator, left, right, leftSet, rightSet);
	}

	@Override
	public long[] merge(ObjectLocator leftLocator, ObjectLocator rightLocator, long[] left, long[] right,
		boolean leftSet, boolean rightSet)
	{
		return getMergeStrategy().merge(leftLocator, rightLocator, left, right, leftSet, rightSet);
	}

	@Override
	public short[] merge(ObjectLocator leftLocator, ObjectLocator rightLocator, short[] left, short[] right,
		boolean leftSet, boolean rightSet)
	{
		return getMergeStrategy().merge(leftLocator, rightLocator, left, right, leftSet, rightSet);
	}

	@Override
	public Object[] merge(ObjectLocator leftLocator, ObjectLocator rightLocator, Object[] left, Object[] right,
		boolean leftSet, boolean rightSet)
	{
		return getMergeStrategy().merge(leftLocator, rightLocator, left, right, leftSet, rightSet);
	}
}
