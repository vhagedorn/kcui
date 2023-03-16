/*
 * Copyright (c) 1995, 2011, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package me.vadim.ja.kc.render;

import com.ruiyun.jvppeteer.protocol.DOM.Margin;
import com.ruiyun.jvppeteer.util.Helper;
import com.ruiyun.jvppeteer.util.StringUtil;
import com.ruiyun.jvppeteer.util.ValidateUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Float version of {@link java.awt.Insets}.
 *
 * @author Arthur van Hoff
 * @author Sami Shaio
 * @author vadim
 */
public class Margins implements Cloneable {

	/**
	 * <a href="https://github.com/fanyong920/jvppeteer/blob/7870fe58205b81f409d72df2204ba5a44bc411e2/src/main/java/com/ruiyun/jvppeteer/core/page/Page.java#L146">source</a>
	 */
	private static final Map<String, Double> unitToPixels = new HashMap<String, Double>() {
		private static final long serialVersionUID = -4861220887908575532L;

		{
			put("px", 1.00);
			put("in", 96.00);
			put("cm", 37.8);
			put("mm", 3.78);
		}
	};

	public String unit;
	public float  left, right, top, bottom;

	public Margins(String unit, float left, float right, float top, float bottom) {
		this.unit   = unit;
		this.left   = left;
		this.right  = right;
		this.top    = top;
		this.bottom = bottom;
	}

	public void set(float left, float right, float top, float bottom) {
		this.left   = left;
		this.right  = right;
		this.top    = top;
		this.bottom = bottom;
	}

	public boolean equals(Object obj) {
		if (obj instanceof Margins) {
			Margins margins = (Margins) obj;
			return
					Objects.equals(convertPrintParameterToInches(top + unit), convertPrintParameterToInches(margins.top + margins.unit)) &&
					Objects.equals(convertPrintParameterToInches(bottom + unit), convertPrintParameterToInches(margins.bottom + margins.unit)) &&
					Objects.equals(convertPrintParameterToInches(left + unit), convertPrintParameterToInches(margins.left + margins.unit)) &&
					Objects.equals(convertPrintParameterToInches(right + unit), convertPrintParameterToInches(margins.right + margins.unit));
		}
		return false;
	}

	public Margin toMargin() {
		Margin margin = new Margin();
		margin.setTop(top + unit);
		margin.setBottom(bottom + unit);
		margin.setLeft(left + unit);
		margin.setRight(right + unit);
		return margin;
	}

	/**
	 * <a href="https://github.com/fanyong920/jvppeteer/blob/7870fe58205b81f409d72df2204ba5a44bc411e2/src/main/java/com/ruiyun/jvppeteer/core/page/Page.java#L1770">source</a>
	 */
	private Double convertPrintParameterToInches(String parameter) {
		if (StringUtil.isEmpty(parameter)) {
			return null;
		}
		double pixels;
		if (Helper.isNumber(parameter)) {
			pixels = Double.parseDouble(parameter);
		} else if (parameter.endsWith("px") || parameter.endsWith("in") || parameter.endsWith("cm") || parameter.endsWith("mm")) {

			String unit = parameter.substring(parameter.length() - 2).toLowerCase();
			String valueText;
			if (unitToPixels.containsKey(unit)) {
				valueText = parameter.substring(0, parameter.length() - 2);
			} else {
				// In case of unknown unit try to parse the whole parameter as number of pixels.
				// This is consistent with phantom's paperSize behavior.
				unit      = "px";
				valueText = parameter;
			}
			double value = Double.parseDouble(valueText);
			ValidateUtil.assertArg(!Double.isNaN(value), "Failed to parse parameter value: " + parameter);
			pixels = value * unitToPixels.get(unit);
		} else {
			throw new IllegalArgumentException("Margins cannot handle unit: " + parameter);
		}
		return pixels / 96;
	}

	public int hashCode() {
		float sum1 = left + bottom;
		float sum2 = right + top;
		float val1 = sum1 * (sum1 + 1) / 2 + left;
		float val2 = sum2 * (sum2 + 1) / 2 + top;
		float sum3 = val1 + val2;
		return Math.round(sum3 * (sum3 + 1) / 2 + val2);
	}

	public String toString() {
		return getClass().getName() + "[unit='" + unit + "',top=" + top + ",left=" + left + ",bottom=" + bottom + ",right=" + right + "]";
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e);
		}
	}

}
