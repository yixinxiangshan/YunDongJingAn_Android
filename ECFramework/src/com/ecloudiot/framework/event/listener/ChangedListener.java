package com.ecloudiot.framework.event.listener;

import com.ecloudiot.framework.event.linterface.OnChangedListener;
import com.ecloudiot.framework.widget.BaseWidget;
import com.michaelnovakjr.numberpicker.NumberPicker;

public class ChangedListener extends BaseEventListener implements OnChangedListener {
	private Object widget;

	public ChangedListener(Object pageContext, Object widget, String eventConfigString) {
		super(pageContext, widget, eventConfigString);
		this.widget = widget;
	}

	@Override
	public void onChanged(NumberPicker picker, int oldVal, int newVal) {
		((BaseWidget) widget).putParam("newVal", Integer.toString(newVal));
		runJs();
	}

	@Override
	public void onChanged(NumberPicker picker, int oldVal, int newVal,
			int position) {
		((BaseWidget) widget).putParam("newVal" + "[position(" + position + ")]", Integer.toString(newVal));
		((BaseWidget) widget).putParam("oldVal" + "[position(" + position + ")]", Integer.toString(oldVal));
		setEventConfigString(matchPosition(getEventConfigString(), "position", position));
		runJs();
		setEventConfigString(matchPosition(getEventConfigString(), position, "position"));
	}
}