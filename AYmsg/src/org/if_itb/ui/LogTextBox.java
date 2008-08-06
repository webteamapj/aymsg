/* 
 * Copyright (C) 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.if_itb.ui;

import android.widget.TextView;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.text.method.MovementMethod;
import android.text.Editable;
import android.util.AttributeSet;

import java.util.Map;

/**
 * This is a TextView that is Editable and by default scrollable,
 * like EditText without a cursor.
 *
 * <p>
 * <b>XML attributes</b>
 * <p>
 * See
 * {@link android.R.styleable#TextView TextView Attributes},
 * {@link android.R.styleable#View View Attributes}
 */
public class LogTextBox extends TextView {
	private int line=0;
	
    public LogTextBox(Context context) {
        this(context, null, null);
    }

    public LogTextBox(Context context, AttributeSet attrs, Map inflateParams) {
        this(context, attrs, inflateParams, android.R.attr.textViewStyle);
    }

    public LogTextBox(Context context, AttributeSet attrs, Map inflateParams,
                      int defStyle) {
        super(context, attrs, inflateParams, defStyle);
    }

    public void appendText(CharSequence text)
	{
		// TODO Auto-generated method stub
		this.append(text);
		this.line++;		
	}

    @Override
    public Editable getText() {
        return (Editable) super.getText();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, BufferType.EDITABLE);
    }

    @Override
    protected boolean getDefaultEditable() {
        return true;
    }

	/* (non-Javadoc)
	 * @see android.widget.TextView#append(java.lang.CharSequence, int, int)
	 */
	
	@Override
    protected MovementMethod getDefaultMovementMethod() {
        return ScrollingMovementMethod.getInstance();
    }
	
	
    
}
