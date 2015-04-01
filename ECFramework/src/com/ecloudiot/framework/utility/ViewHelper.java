package com.ecloudiot.framework.utility;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.ecloudiot.framework.javascript.JsUtility;

public class ViewHelper {

	private static String whitespace = null, characterEncoding = null, identifier, operators = null, attributes = null, pseudos = null;

	@SuppressWarnings("unused")
	private static Pattern rtrim = null, rcomma = null, rcombinators = null, rpseudo = null, ridentifier = null;
	//
	static Pattern rsibling = null, rnative = null,
	// Easily-parseable/retrievable ID or TAG or CLASS selectors
			rquickExpr = null, rinputs = null, rheader = null, rescape = null, rattributeQuotes = null,
			// CSS escapes
			// http://www.w3.org/TR/CSS21/syndata.html#escaped-characters
			runescape = null;

	// funescape = function( _, escaped ) {
	// var high = "0x" + escaped - 0x10000;
	// // NaN means non-codepoint
	// return high !== high ?
	// escaped :
	// // BMP codepoint
	// high < 0 ?
	// String.fromCharCode( high + 0x10000 ) :
	// // Supplemental Plane codepoint (surrogate pair)
	// String.fromCharCode( high >> 10 | 0xD800, high & 0x3FF | 0xDC00 );
	// };
	//

	// ^((?:\\.|[\w*-]|[^\x00-\xa0])+)
	/*
	 * 下面开始写表达式解析器 ；空格 .class
	 */

	static String Tag = "ViewHelper";

	public static void prevEach(View view, TDelegateViewProcess<?, ?> p) {
		int viewIndex = index(view);
		if (viewIndex < 0)
			return;

		ViewGroup pv = (ViewGroup) view.getParent();
		for (int i = viewIndex - 1; i >= 0; i--) {
			if (p.cancel(pv.getChildAt(i), i, 0, pv.getChildCount(), null))
				break;
		}

	}

	public static void parentEach(View view, TDelegateViewProcess<?, ?> p) {
		ViewParent viewParent = view.getParent();
		if (viewParent == null)
			return;

		ViewGroup pv = (ViewGroup) view.getParent();
		int count = pv.getChildCount();
		for (int i = 0; i < count; i--) {
			if (p.cancel(pv.getChildAt(i), i, 0, count, null))
				break;
		}

	}

	public static void nextEach(View view, TDelegateViewProcess<?, ?> p) {
		int viewIndex = index(view);
		if (viewIndex < 0)
			return;

		ViewGroup pv = (ViewGroup) view.getParent();
		int count = pv.getChildCount();
		for (int i = viewIndex + 1; i < count; i++) {
			if (p.cancel(pv.getChildAt(i), i, 0, count, null))
				break;
		}

	}

	public static int index(View view) {
		if (view == null)
			return -1;

		ViewParent viewParent = view.getParent();
		if (viewParent != null && viewParent instanceof ViewGroup) {
			ViewGroup pv = ((ViewGroup) view.getParent());
			View current = null;
			for (int i = 0; i < pv.getChildCount(); i++) {
				current = pv.getChildAt(i);
				if (view.equals(current)) {
					return i;
				}
			}
		}
		return -1;
	}

	// 基础方法
	public static ArrayList<View> parents(View view, TDelegateViewProcess<?, ?> p) {
		ViewGroup parent = null;
		int i = 0;
		while ((parent = (ViewGroup) view.getParent()) != null) {
			if (p.cancel(parent, null, i, -1, null))
				break;
			++i;
		}
		return p.items();
	}

	public static void eachAll(ArrayList<View> views, TDelegateViewProcess<?, ?> p) {
		each(views, p, true);
	}

	// 基础2方法
	public static void each(ArrayList<View> views, TDelegateViewProcess<?, ?> p, boolean findChild) {
		if (views == null || p == null)

			return;

		for (int j = 0; j < views.size(); j++) {
			View child = views.get(j);
			if (p.cancel(child, j, 0, views.size(), null))
				return;
			if (findChild)
				each(child, p, findChild);
		}
	}

	public static void each(View view, TDelegateViewProcess<?, ?> p) {
		each(view, p, true);
	}

	public static void each(View view, TDelegateViewProcess<?, ?> p, boolean findChild) {
		if (view instanceof ViewGroup) {
			ViewGroup viewGroup = (ViewGroup) view;

			for (int j = 0; j < viewGroup.getChildCount(); j++) {
				View child = viewGroup.getChildAt(j);
				if (p.cancel(child, j, 0, viewGroup.getChildCount(), null))
					return;
				if (findChild)
					each(child, p, findChild);
			}
		}
	}

	/********************* 表达式相关 **************/
	static Hashtable<String, IViewStringExpression> find_ht = new Hashtable<String, IViewStringExpression>();
	static Hashtable<String, DelegateFilterExpression> filter_ht = new Hashtable<String, DelegateFilterExpression>();
	static Hashtable<String, IDelegatePositionProcess> position_ht = new Hashtable<String, IDelegatePositionProcess>();
	static Hashtable<String, Pattern> matchExpr = new Hashtable<String, Pattern>();
	static Hashtable<String, IDelegateMatchString> preFilter_ht = new Hashtable<String, IDelegateMatchString>();
	// relative
	static Hashtable<String, JRelativePath> exprRelative_ht = new Hashtable<String, JRelativePath>();

	// IDelegateMatchString

	static void initValues() {
		whitespace = "[\\x20\\t\\r\\n\\f]";

		characterEncoding = "(?:\\\\.|[\\w-]|[^\\x00-\\xa0])+";

		identifier = characterEncoding.replace("w", "w#");

		operators = "([*^$|!~]?=)";
		attributes = "\\[" + whitespace + "*(" + characterEncoding + ")" + whitespace + "*(?:" + operators + whitespace + "*(?:(['\"])((?:\\\\.|[^\\\\])*?)\\3|(" + identifier + ")|)|)" + whitespace
				+ "*\\]";
		LogUtil.d(Tag, attributes);
		pseudos = ":(" + characterEncoding + ")(?:\\(((['\"])((?:\\\\.|[^\\\\])*?)\\3|((?:\\\\.|[^\\\\\\(\\)\\[]|" + attributes.replace('3', '8') + ")*)|.*)\\)|)";

		// [^\\\\\(\)\[]+
		// pseudos = ":("
		// + characterEncoding
		// + ")(?:\\(((['\"])((?:\\\\.|[^\\\\])*?)\\3|((?:\\\\.|[^\\\\()[\\]]|"
		// + attributes.replace('3', '8') + ")*)|.*)\\)|)";

		rtrim = Pattern.compile("^" + whitespace + "+|((?:^|[^\\\\])(?:\\\\.)*)" + whitespace + "+$");

		rcomma = Pattern.compile("^" + whitespace + "*," + whitespace + "*");
		rcombinators = Pattern.compile("^" + whitespace + "*([\\x20\\t\\r\\n\\f>+~])" + whitespace + "*");
		LogUtil.d(Tag, pseudos);
		rpseudo = Pattern.compile(pseudos);
		ridentifier = Pattern.compile("^" + identifier + "$");

		rsibling = Pattern.compile("[\\x20\t\r\n\f]*[+~]");

		rnative = Pattern.compile("^[^{]+\\{\\s*\\[native code");

		// Easily-parseable/retrievable ID or TAG or CLASS selectors
		rquickExpr = Pattern.compile("^(?:#([\\w-]+)|(\\w+)|\\.([\\w-]+))$");

		rinputs = Pattern.compile("^(?:input|select|textarea|button)$");
		rheader = Pattern.compile("^h\\d$/i");

		rescape = Pattern.compile("'|\\\\");
		rattributeQuotes = Pattern.compile(" \\=[\\x20\\t\\r\\n\\f]*([^'\"\\]]*)[\\x20\\t\\r\\n\\f]*\\]");

		// CSS escapes
		// http://www.w3.org/TR/CSS21/syndata.html#escaped-characters
		runescape = Pattern.compile("\\\\([\\da-fA-F]{1,6}[\\x20\\t\\r\\n\\f]?|.)");

	}

	static {

		initValues();

		initExprFind();
		initMatchExpr();
		initExpr();
		initPosition();
	}

	static void initPosition() {
		// ID
		/******** position *********/
		position_ht.put("first", new IDelegatePositionProcess() {

			@Override
			public ArrayList<Integer> InCollection(ArrayList<Integer> matchIndexes, int length, Integer value, ValueMatch valueMatch) {
				if (length > 0)
					matchIndexes.add(0);
				return matchIndexes;
			}
		});

		position_ht.put("last", new IDelegatePositionProcess() {

			@Override
			public ArrayList<Integer> InCollection(ArrayList<Integer> matchIndexes, int length, Integer value, ValueMatch valueMatch) {

				if (length > 0)
					matchIndexes.add(length - 1);
				return matchIndexes;
			}
		});

		position_ht.put("eq", new IDelegatePositionProcess() {

			@Override
			public ArrayList<Integer> InCollection(ArrayList<Integer> matchIndexes, int length, Integer value, ValueMatch valueMatch) {
				if (value != null && length > 0 && value >= 0 && value <= (length - 1)) {
					matchIndexes.add(value);
				}
				return matchIndexes;
			}
		});

		position_ht.put("lt", new IDelegatePositionProcess() {

			@Override
			public ArrayList<Integer> InCollection(ArrayList<Integer> matchIndexes, int length, Integer value, ValueMatch valueMatch) {

				if (value != null && value < length) {
					while ((--value) >= 0) {
						matchIndexes.add(value);
					}
				}
				return matchIndexes;
			}
		});

		position_ht.put("gt", new IDelegatePositionProcess() {
			@Override
			public ArrayList<Integer> InCollection(ArrayList<Integer> matchIndexes, int length, Integer value, ValueMatch valueMatch) {
				if (value != null && value < length) {
					while ((++value) >= 0 && value < length) {
						matchIndexes.add(value);
					}
				}
				return matchIndexes;
			}
		});

		position_ht.put("even", new IDelegatePositionProcess() {
			@Override
			public ArrayList<Integer> InCollection(ArrayList<Integer> matchIndexes, int length, Integer value, ValueMatch valueMatch) {
				int i = 0;

				while (i < length) {
					matchIndexes.add(i);
					i += 2;
				}

				return matchIndexes;
			}
		});

		position_ht.put("odd", new IDelegatePositionProcess() {
			@Override
			public ArrayList<Integer> InCollection(ArrayList<Integer> matchIndexes, int length, Integer value, ValueMatch valueMatch) {
				int i = 1;
				while (i < length) {
					matchIndexes.add(i);
					i += 2;
				}
				return matchIndexes;
			}
		});
	}

	static void initExpr() {
		exprRelative_ht.put(">", new JRelativePath("parentNode", true));
		exprRelative_ht.put(" ", new JRelativePath("parentNode", false));
		exprRelative_ht.put("+", new JRelativePath("previousSibling", true));
		exprRelative_ht.put("~", new JRelativePath("previousSibling", false));
	}

	static void initPreFilter() {
		preFilter_ht.put("ATTR", new IDelegateMatchString() {

			@Override
			public String[] getValue(Matcher matcher) {
				return new String[0];
			}
		});
		// CHILD
		preFilter_ht.put("CHILD", new IDelegateMatchString() {

			@Override
			public String[] getValue(Matcher matcher) {
				return new String[0];
			}
		});
		preFilter_ht.put("PSEUDO", new IDelegateMatchString() {

			@Override
			public String[] getValue(Matcher matcher) {
				// var excess,
				// unquoted = !match[5] && match[2];
				//
				// if ( matchExpr["CHILD"].test( match[0] ) ) {
				// return null;
				// }
				//
				// // Accept quoted arguments as-is
				// if ( match[4] ) {
				// match[2] = match[4];
				//
				// // Strip excess characters from unquoted arguments
				// } else if ( unquoted && rpseudo.test( unquoted ) &&
				// // Get excess from tokenize (recursively)
				// (excess = tokenize( unquoted, true )) &&
				// // advance to the next closing parenthesis
				// (excess = unquoted.indexOf( ")", unquoted.length - excess ) -
				// unquoted.length) ) {
				//
				// // excess is a negative index
				// match[0] = match[0].slice( 0, excess );
				// match[2] = unquoted.slice( 0, excess );
				// }
				//
				// // Return only captures needed by the pseudo filter method
				// (type and argument)
				// return match.slice( 0, 3 )
				return null;
			}
		});

	}

	static String getResourceNameByView(View view) {
		int id = view.getId();
		if (id <= 0)
			return "";

		return JsUtility.GetActivityContext().getResources().getResourceEntryName(id);
	}

	@SuppressWarnings("unused")
	static void initExprFind() {
		find_ht.put("ID", new IViewStringExpression() {

			@Override
			public View getView(String t, View v) {

				String resourceName = getResourceNameByView(v);
				if (resourceName.equals(""))
					return null;

				if (t.startsWith("#"))
					t = t.substring(1);

				return resourceName.equals(t) ? v : null;
			}
		});

		/******************* 此处需要更改 *****************/
		DelegateFilterExpression filter_id = new DelegateFilterExpression() {

			@Override
			public boolean is(View view, String name, String operator, String check, Object[] values) {

				String value = JsUtility.GetActivityContext().getResources().getResourceEntryName(view.getId());

				return value == name;
			}
		};
		filter_ht.put("ID", filter_id);
		// 需要改动
		DelegateFilterExpression filter_Class = new DelegateFilterExpression() {

			@Override
			public boolean is(View view, String name, String operator, String check, Object[] values) {
				Object tag = view.getTag();

				if (tag instanceof String && tag != null) {
					String[] names = ((String) tag).split("[\\s]+");

					if (name.startsWith("."))
						name = name.substring(1);
					boolean success = StringUtil.inArray(names, name, false);
					return success;
				}
				return false;
			}
		};

		filter_ht.put("CLASS", filter_Class);

		DelegateFilterExpression filter_CHILD = new DelegateFilterExpression() {

			@Override
			public boolean is(View view, String name, String operator, String check, Object[] values) {
				return false;
			}
		};
		filter_ht.put("CHILD", filter_Class);

		DelegateFilterExpression filter_PSEUDO = new DelegateFilterExpression() {

			@Override
			public boolean is(View view, String name, String operator, String check, Object[] values) {
				return false;
			}
		};

		filter_ht.put("PSEUDO", filter_Class);

		// PSEUDO

		// TAG

		DelegateFilterExpression filter_Tag = new DelegateFilterExpression() {

			@Override
			public boolean is(View view, String name, String operator, String check, Object[] values) {

				String[] names = view.getClass().getName().split("\\.");

				return names[names.length - 1].equalsIgnoreCase(name);

			}
		};

		filter_ht.put("TAG", filter_Tag);

		IViewStringExpression find_tag = new IViewStringExpression() {

			@Override
			public View getView(String t, View v) {
				String className = v.getClass().getName().toLowerCase();

				boolean success = className.endsWith(t.toLowerCase());
				return success ? v : null;

			}

		};
		find_ht.put("TAG", find_tag);
		IViewStringExpression find_Class = new IViewStringExpression() {

			@Override
			public View getView(String t, View context) {

				Object tagValue = context.getTag();

				if (!(tagValue instanceof String) || ((String) tagValue) == null) {
					LogUtil.d("ViewTag", "find_tag:NULL");
					return null;
				}

				else
					LogUtil.d("ViewTag", "tags:" + tagValue.toString());

				if (t.startsWith("."))
					t = t.substring(1);
				String[] names = (tagValue).toString().split("\\s+");
				boolean success = StringUtil.inArray(names, t, false);
				return success ? context : null;

			}
		};
		find_ht.put("CLASS", find_Class);

		// 没用
		IViewStringExpression find_Name = new IViewStringExpression() {

			@Override
			public View getView(String t, View context) {

				String[] names = context.getClass().getName().split("\\.");

				if (names[names.length - 1].equalsIgnoreCase(t))
					return context;
				return null;
			}
		};
		find_ht.put("Name", find_Name);
	}

	static void initMatchExpr() {

		// characterEncoding="";

		matchExpr.put("ID", Pattern.compile("^#(" + characterEncoding + ")"));
		LogUtil.d(Tag, characterEncoding == null ? "null" : characterEncoding);
		matchExpr.put("CLASS", Pattern.compile("^\\.(" + characterEncoding + ")"));
		matchExpr.put("NAME", Pattern.compile("^\\[name=['\"]?(" + characterEncoding + ")['\"]?\\]"));
		matchExpr.put("TAG", Pattern.compile("^(" + characterEncoding.replace("w", "w*") + ")"));
		matchExpr.put("ATTR", Pattern.compile("^" + attributes));
		matchExpr.put("PSEUDO", Pattern.compile("^" + pseudos));
		matchExpr.put(
				"CHILD",
				Pattern.compile("^:(only|first|last|nth|nth-last)-(child|of-type)(?:\\(" + whitespace + "*(even|odd|(([+-]|)(\\d*)n|)" + whitespace + "*(?:([+-]|)" + whitespace + "*(\\d+)|))"
						+ whitespace + "*\\)|)"));
		matchExpr.put("needsContext", Pattern.compile("^" + whitespace + "*[>+~]|:(even|odd|eq|gt|lt|nth|first|last)(?:\\(" + whitespace + "*((?:-\\d)?\\d*)" + whitespace + "*\\)|)(?=[^-]|$)"));

	}

	public static ArrayList<View> find(View view, String selector) {
		if (view instanceof ViewGroup)
			return find((ViewGroup) view, selector);

		return new ArrayList<View>();
	}

	public static ArrayList<View> find(ArrayList<View> views, String selector) {
		if (selector != null) {
			selector = selector.trim();
		}

		ArrayList<View> newViews = find(views, selector, 1);
		return newViews;
	}

	public static ArrayList<View> find(ViewGroup viewGroup, String selector) {
		selector = selector.trim();

		ArrayList<View> views = new ArrayList<View>();
		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			views.add(viewGroup.getChildAt(i));
		}
		ArrayList<View> newViews = find(views, selector, 1);
		return newViews;
	}

	private static String[] matcherToArray(Matcher matcher) {
		if (matcher == null)
			return null;

		String[] values = new String[matcher.groupCount()];
		for (int i = 0; i != values.length; i++) {
			values[i] = matcher.group(i);
		}
		return values;
	}

	static boolean matchHasValue(Matcher match) {
		if (match == null)
			return false;

		boolean isFind = match.find();
		int groupCount = match.groupCount();
		LogUtil.d(Tag, "matchHasValue.groupCount:" + groupCount);
		LogUtil.d(Tag, "matchHasValue.isFind:" + isFind);
		if (isFind) {
			LogUtil.d(Tag, "matchHasValue.Value:" + match.group(0));
		}
		return isFind;
	}

	public static ArrayList<ArrayList<ValueMatch>> tokenize(String selector) {

		String soFar = selector;
		ArrayList<ArrayList<ValueMatch>> groups = new ArrayList<ArrayList<ValueMatch>>();
		// 此成员类型需要变更
		ArrayList<ValueMatch> tokens = null;

		// preFilters = Expr.preFilter;
		Matcher match = null;

		Boolean matched = false;
		String value = null;
		String[] group = null;
		int max = 100;
		String prevValue = selector;
		boolean isFind;
		while (soFar != null && soFar != "") {
			value = null;
			isFind = false;
			// Comma and first run
			if (!matched || (isFind = matchHasValue(match = rcomma.matcher(soFar)))) {

				if (isFind) {
					// Don't consume trailing commas as valid
					value = soFar.substring(match.group(0).length());
					soFar = value.length() > 0 ? value : soFar;
				}
				groups.add((tokens = new ArrayList<ValueMatch>()));

			}

			// Combinators
			if (matchHasValue(match = rcombinators.matcher(soFar))) {

				value = match.group(0);
				matched = true;
				// 这里需要改变下 复合表达式
				ValueMatch item = new ValueMatch(value.replace(rtrim.pattern(), " "), value);

				tokens.add(item);
				soFar = soFar.substring(value.length());
				soFar = soFar.replaceFirst(rtrim.pattern(), "");
			}

			// Filters

			Enumeration<String> keys = filter_ht.keys();
			String type = null;
			LogUtil.d("tokenize", "选择器：" + selector + "  解析:");
			while (keys.hasMoreElements()) {
				type = keys.nextElement();
				Pattern pattern = matchExpr.get(type);
				match = pattern.matcher(soFar);

				int groupCount = -1;
				if (match != null) {
					groupCount = match.groupCount();
				}
				LogUtil.d(Tag, "match_Value" + groupCount);

				if (matchHasValue(match)) {

					if (preFilter_ht.containsKey(type)) {

						IDelegateMatchString matchString = preFilter_ht.get(type);

						group = matchString.getValue(match);
						if (group == null || group.length == 0)
							continue;

					} else {
						group = matcherToArray(match);
					}

					value = (group != null && group.length > 0) ? group[0] : "";

					LogUtil.d(Tag, "tokenize_" + type + " value:" + value + " group:" + ((group == null) ? "NULL" : StringUtil.join(group, "|")));

					// ???????????????
					ValueMatch valueMatch = new ValueMatch(type, value, group);
					tokens.add(valueMatch);

					soFar = soFar.substring(value.length());
					soFar = soFar.replaceFirst(rtrim.pattern(), "");
					matched = true;

				}
			}

			if (soFar == "" || prevValue == soFar) // 解析完|无法解析
				break;

			if (max <= 0)// 最大限制次数 测试
				break;
			--max;
		}
		return groups;
	}

	public static ArrayList<View> find(ArrayList<View> context, String selector, int direction) {

		if (selector == null || selector.equals("") || context == null || context.size() == 0)
			return context;

		// direction self=0 children=1 childrenAll=2
		ArrayList<ArrayList<ValueMatch>> rows = tokenize(selector);

		ArrayList<View> result = new ArrayList<View>();
		ArrayList<ValueMatch> row = null;
		int len = rows.size();
		for (int i = 0; i < len; i++) {
			row = rows.get(i);
			ArrayList<View> views = searchViews(row, context, 1);
			if (views != null) {
				for (int viewIndex = 0; viewIndex < views.size(); viewIndex++) {
					result.add(views.get(viewIndex));
				}
			}
		}
		return result;
	}

	public static ArrayList<View> searchViews(ArrayList<ValueMatch> matchValues, ArrayList<View> context, int direction) {
		int len = 0;
		if (matchValues == null)
			return context;
		len = matchValues.size();
		if (len == 0)
			return context;

		int tempDirection = direction;

		ArrayList<View> tempViews = null;

		for (int i = 0; i != len; i++) {
			if (i == 0) {
				tempViews = context;
			}

			final ValueMatch valueMatch = matchValues.get(i);

			Hashtable<String, Integer> ht = new Hashtable<String, Integer>();
			ht.put(" ", 1);
			ht.put(">", 2);
			ht.put("", 0);

			// 表达式 判断
			if (ht.containsKey(valueMatch.Value)) {
				tempDirection = ht.get(valueMatch.Value);
			}
			LogUtil.d(Tag, "searchViews_:valuematch.type=" + valueMatch.Type + " valuematch.value=" + (valueMatch.Value == null ? "NULL" : ""));

			/*
			 * 1.除了 伪类 索引 定义 是自己 这个view 2.其它深层迭代
			 */

			if (find_ht.containsKey(valueMatch.Type)) {
				final IViewStringExpression expr = find_ht.get(valueMatch.Type);

				LogUtil.d(Tag, "searchViews_:find_ht.containsKey=" + valueMatch.Type + " INDEX=" + i);

				DelegateViewProcess p1 = new DelegateViewProcess() {
					IViewStringExpression expression = expr;
					ValueMatch match = valueMatch;

					@Override
					public Boolean cancel(View view, Integer index, Integer depth, Integer size, Object args) {
						View v = expression.getView(match.Value, view);
						if (v != null)
							this.add(v);
						return false;
					}
				};

				if (tempDirection == 0) {

					for (int j = 0; j < tempViews.size(); j++) {
						View child = tempViews.get(j);
						p1.cancel(child, j, 0, tempViews.size(), null);
					}
					tempViews = p1.items();
					LogUtil.d(Tag, "direction == 0: " + tempViews.size());

				} else if (tempDirection == -1) {
					for (int j = 0; j < tempViews.size(); j++) {
						View child = tempViews.get(j);
						parentEach(child, p1);
						// p1.cancel(child, j, 0, tempViews.size(), null);
					}
					tempViews = p1.items();
					LogUtil.d(Tag, "direction == -1: " + tempViews.size());
				} else if (tempDirection == 2 || tempDirection == 1) {

					eachAll(tempViews, p1);
					tempViews = p1.items();
					LogUtil.d(Tag, "direction == 2: " + tempViews.size());

				}
				// else if(tempDirection == 1) {
				//
				// each(tempViews, p1);
				// tempViews = p1.items();
				// LogUtil.d(Tag, "direction == 1: " + tempViews.size());
				//
				// }
				// result.addAll(tempViews);
				continue;
			}

			// 定位
			if (valueMatch.Type.equals("PSEUDO")) {
				String value = valueMatch.Group.length > 2 ? valueMatch.Group[1] : "";

				if (position_ht.containsKey(value)) {

					LogUtil.d(Tag, "searchViews_:find_ht.containsKey=" + valueMatch.Value);

					ArrayList<View> currentViews = new ArrayList<View>();

					ArrayList<Integer> matchIndexes = new ArrayList<Integer>();

					ExtractPositionIndexs(matchIndexes, tempViews.size(), valueMatch);

					for (int viewIndex = 0; viewIndex < matchIndexes.size(); viewIndex++) {
						View view = tempViews.get(matchIndexes.get(viewIndex));
						if (!currentViews.contains(view)) {
							currentViews.add(view);
						}
					}
					tempViews = currentViews;
					continue;

				}
			}

		}
		return tempViews;
	}

	// ArrayList<ValueMatch> positionMatchs
	private static void ExtractPositionIndexs(ArrayList<Integer> matchIndexes, int len, ValueMatch valueMatch) {

		// valueMatch.Value;
		// if (value == null || value.length()==0)
		// value = "";
		// else
		// value = value.trim();
		// 0=all 1=name 2=params
		String positionName = "", value = null;
		if (valueMatch.Group != null) {
			if (valueMatch.Group.length >= 2) {
				positionName = valueMatch.Group[1];
			}

			if (valueMatch.Group.length >= 3) {
				value = valueMatch.Group[2];
			}
		}

		if (value == null)
			value = "";
		value = value.trim();

		Integer positionValue = Pattern.matches("^\\d+$", value) ? Integer.parseInt(value) : null;
		IDelegatePositionProcess p = null;

		if (value.length() != 0 && positionValue == null) {
			if (valueMatch.Group != null && valueMatch.Group.length >= 2) {
				ArrayList<ArrayList<ValueMatch>> positionTokenize = tokenize(valueMatch.Group[1]);

				if (positionTokenize.size() == 1) {
					ArrayList<ValueMatch> childrenValueMatchs = positionTokenize.get(0);

					for (int i = 0; i != childrenValueMatchs.size(); i++) {
						ExtractPositionIndexs(matchIndexes, len, childrenValueMatchs.get(i));
					}
				}
			}
		}

		if (position_ht.containsKey(positionName)) {
			p = position_ht.get(positionName);
			p.InCollection(matchIndexes, len, positionValue, valueMatch);
			LogUtil.d(Tag, "position_ht_matchIndexes:" + matchIndexes.size());
		}

	}

	// public static ArrayList<Object> _select(String selector, ArrayList<View>
	// context,ArrayList<Object> result, Boolean seed)
	// {
	//
	// // var i, , token, type, find,
	// // match = tokenize( selector );
	// ArrayList<ArrayList<ValueMatch>> matchValues = tokenize(selector);
	// ArrayList<ValueMatch> tokens = null;
	// ValueMatch token = null;
	// if(seed!=null && seed)
	// {
	// if(matchValues!=null && matchValues.size()==1)
	// {
	// tokens=matchValues.get(0);
	// if(tokens.size()>2 && (token= tokens.get(0)).Type=="ID" )
	// {
	// // 运算表达式未做 Expr.relative[ tokens[1].type ]
	// // context = Expr.find["ID"]( token.matches[0].replace( runescape,
	// funescape ), context )[0];
	// // if ( !context ) {
	// // return result;
	// // }
	// //
	// // selector = selector.slice( tokens.shift().value.length );
	// }
	//
	//
	//
	// }
	// String type = null;
	// Pattern pattern = matchExpr.get("needsContext");
	// int i = pattern.matcher(selector).matches()? 0 :tokens.size();
	// while( (i--)>0 )
	// {
	// token = tokens.get(i);
	//
	// if( exprRelative_ht.containsKey((type = token.Type)))
	// break;
	// //替换正确的ID
	// //token.Group[0].replaceAll(runescape, funescape);
	//
	// IViewStringExpression exp =find_ht.get(type);
	// if(exp==null)
	// continue;
	// if( rsibling.matcher(type).matches())
	// {
	//
	// }
	// }
	//
	//
	// }
	//
	// return result;
	// }

}
