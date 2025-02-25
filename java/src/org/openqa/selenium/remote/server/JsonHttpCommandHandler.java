// Licensed to the Software Freedom Conservancy (SFC) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The SFC licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.openqa.selenium.remote.server;

import org.openqa.selenium.UnsupportedCommandException;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.CommandCodec;
import org.openqa.selenium.remote.ErrorCodes;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.ResponseCodec;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.remote.codec.w3c.W3CHttpCommandCodec;
import org.openqa.selenium.remote.codec.w3c.W3CHttpResponseCodec;
import org.openqa.selenium.remote.http.HttpRequest;
import org.openqa.selenium.remote.http.HttpResponse;
import org.openqa.selenium.remote.server.handler.AcceptAlert;
import org.openqa.selenium.remote.server.handler.AddCookie;
import org.openqa.selenium.remote.server.handler.CaptureScreenshot;
import org.openqa.selenium.remote.server.handler.ChangeUrl;
import org.openqa.selenium.remote.server.handler.ClearElement;
import org.openqa.selenium.remote.server.handler.ClickElement;
import org.openqa.selenium.remote.server.handler.CloseWindow;
import org.openqa.selenium.remote.server.handler.ConfigureTimeout;
import org.openqa.selenium.remote.server.handler.DeleteCookie;
import org.openqa.selenium.remote.server.handler.DeleteNamedCookie;
import org.openqa.selenium.remote.server.handler.DeleteSession;
import org.openqa.selenium.remote.server.handler.DismissAlert;
import org.openqa.selenium.remote.server.handler.ElementEquality;
import org.openqa.selenium.remote.server.handler.ExecuteAsyncScript;
import org.openqa.selenium.remote.server.handler.ExecuteScript;
import org.openqa.selenium.remote.server.handler.FindActiveElement;
import org.openqa.selenium.remote.server.handler.FindChildElement;
import org.openqa.selenium.remote.server.handler.FindChildElements;
import org.openqa.selenium.remote.server.handler.FindElement;
import org.openqa.selenium.remote.server.handler.FindElements;
import org.openqa.selenium.remote.server.handler.FullscreenWindow;
import org.openqa.selenium.remote.server.handler.GetAlertText;
import org.openqa.selenium.remote.server.handler.GetAllCookies;
import org.openqa.selenium.remote.server.handler.GetAllSessions;
import org.openqa.selenium.remote.server.handler.GetAllWindowHandles;
import org.openqa.selenium.remote.server.handler.GetAvailableLogTypesHandler;
import org.openqa.selenium.remote.server.handler.GetCookie;
import org.openqa.selenium.remote.server.handler.GetCssProperty;
import org.openqa.selenium.remote.server.handler.GetCurrentUrl;
import org.openqa.selenium.remote.server.handler.GetCurrentWindowHandle;
import org.openqa.selenium.remote.server.handler.GetElementAttribute;
import org.openqa.selenium.remote.server.handler.GetElementDisplayed;
import org.openqa.selenium.remote.server.handler.GetElementEnabled;
import org.openqa.selenium.remote.server.handler.GetElementLocation;
import org.openqa.selenium.remote.server.handler.GetElementLocationInView;
import org.openqa.selenium.remote.server.handler.GetElementRect;
import org.openqa.selenium.remote.server.handler.GetElementSelected;
import org.openqa.selenium.remote.server.handler.GetElementSize;
import org.openqa.selenium.remote.server.handler.GetElementText;
import org.openqa.selenium.remote.server.handler.GetLogHandler;
import org.openqa.selenium.remote.server.handler.GetPageSource;
import org.openqa.selenium.remote.server.handler.GetSessionCapabilities;
import org.openqa.selenium.remote.server.handler.GetSessionLogsHandler;
import org.openqa.selenium.remote.server.handler.GetTagName;
import org.openqa.selenium.remote.server.handler.GetTitle;
import org.openqa.selenium.remote.server.handler.GetWindowPosition;
import org.openqa.selenium.remote.server.handler.GetWindowSize;
import org.openqa.selenium.remote.server.handler.GoBack;
import org.openqa.selenium.remote.server.handler.GoForward;
import org.openqa.selenium.remote.server.handler.ImplicitlyWait;
import org.openqa.selenium.remote.server.handler.MaximizeWindow;
import org.openqa.selenium.remote.server.handler.RefreshPage;
import org.openqa.selenium.remote.server.handler.SendKeys;
import org.openqa.selenium.remote.server.handler.SetAlertText;
import org.openqa.selenium.remote.server.handler.SetScriptTimeout;
import org.openqa.selenium.remote.server.handler.SetWindowPosition;
import org.openqa.selenium.remote.server.handler.SetWindowSize;
import org.openqa.selenium.remote.server.handler.Status;
import org.openqa.selenium.remote.server.handler.SubmitElement;
import org.openqa.selenium.remote.server.handler.SwitchToFrame;
import org.openqa.selenium.remote.server.handler.SwitchToParentFrame;
import org.openqa.selenium.remote.server.handler.SwitchToWindow;
import org.openqa.selenium.remote.server.handler.UploadFile;
import org.openqa.selenium.remote.server.handler.W3CActions;
import org.openqa.selenium.remote.server.handler.html5.ClearLocalStorage;
import org.openqa.selenium.remote.server.handler.html5.ClearSessionStorage;
import org.openqa.selenium.remote.server.handler.html5.GetLocalStorageItem;
import org.openqa.selenium.remote.server.handler.html5.GetLocalStorageKeys;
import org.openqa.selenium.remote.server.handler.html5.GetLocalStorageSize;
import org.openqa.selenium.remote.server.handler.html5.GetSessionStorageItem;
import org.openqa.selenium.remote.server.handler.html5.GetSessionStorageKeys;
import org.openqa.selenium.remote.server.handler.html5.GetSessionStorageSize;
import org.openqa.selenium.remote.server.handler.html5.RemoveLocalStorageItem;
import org.openqa.selenium.remote.server.handler.html5.RemoveSessionStorageItem;
import org.openqa.selenium.remote.server.handler.html5.SetLocalStorageItem;
import org.openqa.selenium.remote.server.handler.html5.SetSessionStorageItem;
import org.openqa.selenium.remote.server.handler.interactions.ClickInSession;
import org.openqa.selenium.remote.server.handler.interactions.DoubleClickInSession;
import org.openqa.selenium.remote.server.handler.interactions.MouseDown;
import org.openqa.selenium.remote.server.handler.interactions.MouseMoveToLocation;
import org.openqa.selenium.remote.server.handler.interactions.MouseUp;
import org.openqa.selenium.remote.server.handler.interactions.SendKeyToActiveElement;
import org.openqa.selenium.remote.server.handler.interactions.touch.DoubleTapOnElement;
import org.openqa.selenium.remote.server.handler.interactions.touch.Down;
import org.openqa.selenium.remote.server.handler.interactions.touch.Flick;
import org.openqa.selenium.remote.server.handler.interactions.touch.LongPressOnElement;
import org.openqa.selenium.remote.server.handler.interactions.touch.Move;
import org.openqa.selenium.remote.server.handler.interactions.touch.Scroll;
import org.openqa.selenium.remote.server.handler.interactions.touch.SingleTapOnElement;
import org.openqa.selenium.remote.server.handler.interactions.touch.Up;
import org.openqa.selenium.remote.server.log.LoggingManager;
import org.openqa.selenium.remote.server.log.PerSessionLogHandler;
import org.openqa.selenium.remote.server.rest.RestishHandler;
import org.openqa.selenium.remote.server.rest.ResultConfig;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Logger;

import static org.openqa.selenium.remote.DriverCommand.ACCEPT_ALERT;
import static org.openqa.selenium.remote.DriverCommand.ACTIONS;
import static org.openqa.selenium.remote.DriverCommand.ADD_COOKIE;
import static org.openqa.selenium.remote.DriverCommand.CLEAR_ELEMENT;
import static org.openqa.selenium.remote.DriverCommand.CLEAR_LOCAL_STORAGE;
import static org.openqa.selenium.remote.DriverCommand.CLEAR_SESSION_STORAGE;
import static org.openqa.selenium.remote.DriverCommand.CLICK_ELEMENT;
import static org.openqa.selenium.remote.DriverCommand.CLOSE;
import static org.openqa.selenium.remote.DriverCommand.DELETE_ALL_COOKIES;
import static org.openqa.selenium.remote.DriverCommand.DELETE_COOKIE;
import static org.openqa.selenium.remote.DriverCommand.DISMISS_ALERT;
import static org.openqa.selenium.remote.DriverCommand.ELEMENT_EQUALS;
import static org.openqa.selenium.remote.DriverCommand.EXECUTE_ASYNC_SCRIPT;
import static org.openqa.selenium.remote.DriverCommand.EXECUTE_SCRIPT;
import static org.openqa.selenium.remote.DriverCommand.FIND_CHILD_ELEMENT;
import static org.openqa.selenium.remote.DriverCommand.FIND_CHILD_ELEMENTS;
import static org.openqa.selenium.remote.DriverCommand.FIND_ELEMENT;
import static org.openqa.selenium.remote.DriverCommand.FIND_ELEMENTS;
import static org.openqa.selenium.remote.DriverCommand.FULLSCREEN_CURRENT_WINDOW;
import static org.openqa.selenium.remote.DriverCommand.GET;
import static org.openqa.selenium.remote.DriverCommand.GET_ACTIVE_ELEMENT;
import static org.openqa.selenium.remote.DriverCommand.GET_ALERT_TEXT;
import static org.openqa.selenium.remote.DriverCommand.GET_ALL_COOKIES;
import static org.openqa.selenium.remote.DriverCommand.GET_ALL_SESSIONS;
import static org.openqa.selenium.remote.DriverCommand.GET_AVAILABLE_LOG_TYPES;
import static org.openqa.selenium.remote.DriverCommand.GET_CAPABILITIES;
import static org.openqa.selenium.remote.DriverCommand.GET_COOKIE;
import static org.openqa.selenium.remote.DriverCommand.GET_CURRENT_URL;
import static org.openqa.selenium.remote.DriverCommand.GET_CURRENT_WINDOW_HANDLE;
import static org.openqa.selenium.remote.DriverCommand.GET_CURRENT_WINDOW_POSITION;
import static org.openqa.selenium.remote.DriverCommand.GET_CURRENT_WINDOW_SIZE;
import static org.openqa.selenium.remote.DriverCommand.GET_ELEMENT_ATTRIBUTE;
import static org.openqa.selenium.remote.DriverCommand.GET_ELEMENT_LOCATION;
import static org.openqa.selenium.remote.DriverCommand.GET_ELEMENT_LOCATION_ONCE_SCROLLED_INTO_VIEW;
import static org.openqa.selenium.remote.DriverCommand.GET_ELEMENT_RECT;
import static org.openqa.selenium.remote.DriverCommand.GET_ELEMENT_SIZE;
import static org.openqa.selenium.remote.DriverCommand.GET_ELEMENT_TAG_NAME;
import static org.openqa.selenium.remote.DriverCommand.GET_ELEMENT_TEXT;
import static org.openqa.selenium.remote.DriverCommand.GET_ELEMENT_VALUE_OF_CSS_PROPERTY;
import static org.openqa.selenium.remote.DriverCommand.GET_LOCAL_STORAGE_ITEM;
import static org.openqa.selenium.remote.DriverCommand.GET_LOCAL_STORAGE_KEYS;
import static org.openqa.selenium.remote.DriverCommand.GET_LOCAL_STORAGE_SIZE;
import static org.openqa.selenium.remote.DriverCommand.GET_LOG;
import static org.openqa.selenium.remote.DriverCommand.GET_PAGE_SOURCE;
import static org.openqa.selenium.remote.DriverCommand.GET_SESSION_LOGS;
import static org.openqa.selenium.remote.DriverCommand.GET_SESSION_STORAGE_ITEM;
import static org.openqa.selenium.remote.DriverCommand.GET_SESSION_STORAGE_KEYS;
import static org.openqa.selenium.remote.DriverCommand.GET_SESSION_STORAGE_SIZE;
import static org.openqa.selenium.remote.DriverCommand.GET_TITLE;
import static org.openqa.selenium.remote.DriverCommand.GET_WINDOW_HANDLES;
import static org.openqa.selenium.remote.DriverCommand.GO_BACK;
import static org.openqa.selenium.remote.DriverCommand.GO_FORWARD;
import static org.openqa.selenium.remote.DriverCommand.IMPLICITLY_WAIT;
import static org.openqa.selenium.remote.DriverCommand.IS_ELEMENT_DISPLAYED;
import static org.openqa.selenium.remote.DriverCommand.IS_ELEMENT_ENABLED;
import static org.openqa.selenium.remote.DriverCommand.IS_ELEMENT_SELECTED;
import static org.openqa.selenium.remote.DriverCommand.MAXIMIZE_CURRENT_WINDOW;
import static org.openqa.selenium.remote.DriverCommand.QUIT;
import static org.openqa.selenium.remote.DriverCommand.REFRESH;
import static org.openqa.selenium.remote.DriverCommand.REMOVE_LOCAL_STORAGE_ITEM;
import static org.openqa.selenium.remote.DriverCommand.REMOVE_SESSION_STORAGE_ITEM;
import static org.openqa.selenium.remote.DriverCommand.SCREENSHOT;
import static org.openqa.selenium.remote.DriverCommand.SEND_KEYS_TO_ELEMENT;
import static org.openqa.selenium.remote.DriverCommand.SET_ALERT_VALUE;
import static org.openqa.selenium.remote.DriverCommand.SET_CURRENT_WINDOW_POSITION;
import static org.openqa.selenium.remote.DriverCommand.SET_CURRENT_WINDOW_SIZE;
import static org.openqa.selenium.remote.DriverCommand.SET_LOCAL_STORAGE_ITEM;
import static org.openqa.selenium.remote.DriverCommand.SET_SCRIPT_TIMEOUT;
import static org.openqa.selenium.remote.DriverCommand.SET_SESSION_STORAGE_ITEM;
import static org.openqa.selenium.remote.DriverCommand.SET_TIMEOUT;
import static org.openqa.selenium.remote.DriverCommand.STATUS;
import static org.openqa.selenium.remote.DriverCommand.SUBMIT_ELEMENT;
import static org.openqa.selenium.remote.DriverCommand.SWITCH_TO_FRAME;
import static org.openqa.selenium.remote.DriverCommand.SWITCH_TO_PARENT_FRAME;
import static org.openqa.selenium.remote.DriverCommand.SWITCH_TO_WINDOW;
import static org.openqa.selenium.remote.DriverCommand.UPLOAD_FILE;

public class JsonHttpCommandHandler {

  private static final String SEND_KEYS_TO_ACTIVE_ELEMENT = "sendKeysToActiveElement";
  // These belong to the Advanced user interactions - an element is
  // optional for these commands.
  private static final String CLICK = "mouseClick";
  private static final String DOUBLE_CLICK = "mouseDoubleClick";
  private static final String MOUSE_DOWN = "mouseButtonDown";
  private static final String MOUSE_UP = "mouseButtonUp";
  private static final String MOVE_TO = "mouseMoveTo";
  // These belong to the Advanced Touch API
  private static final String TOUCH_SINGLE_TAP = "touchSingleTap";
  private static final String TOUCH_DOWN = "touchDown";
  private static final String TOUCH_UP = "touchUp";
  private static final String TOUCH_MOVE = "touchMove";
  private static final String TOUCH_SCROLL = "touchScroll";
  private static final String TOUCH_DOUBLE_TAP = "touchDoubleTap";
  private static final String TOUCH_LONG_PRESS = "touchLongPress";
  private static final String TOUCH_FLICK = "touchFlick";
  private final DriverSessions sessions;
  private final Logger log;
  private final Set<CommandCodec<HttpRequest>> commandCodecs;
  private final ResponseCodec<HttpResponse> responseCodec;
  private final Map<String, ResultConfig> configs = new LinkedHashMap<>();
  private final ErrorCodes errorCodes = new ErrorCodes();

  public JsonHttpCommandHandler(DriverSessions sessions, Logger log) {
    this.sessions = sessions;
    this.log = log;
    this.commandCodecs = new LinkedHashSet<>();
    this.commandCodecs.add(new W3CHttpCommandCodec());
    this.responseCodec = new W3CHttpResponseCodec();
    setUpMappings();
  }

  public void addNewMapping(
      String commandName,
      Supplier<RestishHandler<?>> factory) {
    ResultConfig config = new ResultConfig(commandName, factory, sessions, log);
    configs.put(commandName, config);
  }

  public void addNewMapping(
      String commandName,
      RequiresAllSessions factory) {
    ResultConfig config = new ResultConfig(commandName, factory, sessions, log);
    configs.put(commandName, config);
  }

  public void addNewMapping(
      String commandName,
      RequiresSession factory) {
    ResultConfig config = new ResultConfig(commandName, factory, sessions, log);
    configs.put(commandName, config);
  }

  public void handleRequest(HttpRequest request, HttpResponse resp) {
    LoggingManager.perSessionLogHandler().clearThreadTempLogs();
    log.fine(String.format("Handling: %s %s", request.getMethod(), request.getUri()));

    Command command = null;
    Response response;
    try {
      command = decode(request);
      ResultConfig config = configs.get(command.getName());
      if (config == null) {
        throw new UnsupportedCommandException();
      }
      response = config.handle(command);
      log.fine(String.format("Finished: %s %s", request.getMethod(), request.getUri()));
    } catch (Exception e) {
      log.fine(String.format("Error on: %s %s", request.getMethod(), request.getUri()));
      response = new Response();
      response.setStatus(errorCodes.toStatusCode(e));
      response.setState(errorCodes.toState(response.getStatus()));
      response.setValue(e);

      if (command != null && command.getSessionId() != null) {
        response.setSessionId(command.getSessionId().toString());
      }
    }

    PerSessionLogHandler handler = LoggingManager.perSessionLogHandler();
    if (response.getSessionId() != null) {
      handler.attachToCurrentThread(new SessionId(response.getSessionId()));
    }
    try {
      responseCodec.encode(() -> resp, response);
    } finally {
      handler.detachFromCurrentThread();
    }
  }

  private Command decode(HttpRequest request) {
    UnsupportedCommandException lastException = null;
    for (CommandCodec<HttpRequest> codec : commandCodecs) {
      try {
        return codec.decode(request);
      } catch (UnsupportedCommandException e) {
        lastException = e;
      }
    }
    if (lastException != null) {
      throw lastException;
    }
    throw new UnsupportedOperationException("Cannot find command for: " + request.getUri());
  }

  private void setUpMappings() {
    addNewMapping(STATUS, Status::new);
    addNewMapping(GET_ALL_SESSIONS, GetAllSessions::new);
    addNewMapping(GET_CAPABILITIES, GetSessionCapabilities::new);
    addNewMapping(QUIT, DeleteSession::new);

    addNewMapping(GET_CURRENT_WINDOW_HANDLE, GetCurrentWindowHandle::new);
    addNewMapping(GET_WINDOW_HANDLES, GetAllWindowHandles::new);

    addNewMapping(DISMISS_ALERT, DismissAlert::new);
    addNewMapping(ACCEPT_ALERT, AcceptAlert::new);
    addNewMapping(GET_ALERT_TEXT, GetAlertText::new);
    addNewMapping(SET_ALERT_VALUE, SetAlertText::new);

    addNewMapping(GET, ChangeUrl::new);
    addNewMapping(GET_CURRENT_URL, GetCurrentUrl::new);
    addNewMapping(GO_FORWARD, GoForward::new);
    addNewMapping(GO_BACK, GoBack::new);
    addNewMapping(REFRESH, RefreshPage::new);

    addNewMapping(EXECUTE_SCRIPT, ExecuteScript::new);
    addNewMapping(EXECUTE_ASYNC_SCRIPT, ExecuteAsyncScript::new);

    addNewMapping(GET_PAGE_SOURCE, GetPageSource::new);

    addNewMapping(SCREENSHOT, CaptureScreenshot::new);

    addNewMapping(GET_TITLE, GetTitle::new);

    addNewMapping(FIND_ELEMENT, FindElement::new);
    addNewMapping(FIND_ELEMENTS, FindElements::new);
    addNewMapping(GET_ACTIVE_ELEMENT, FindActiveElement::new);

    addNewMapping(FIND_CHILD_ELEMENT, FindChildElement::new);
    addNewMapping(FIND_CHILD_ELEMENTS, FindChildElements::new);

    addNewMapping(CLICK_ELEMENT, ClickElement::new);
    addNewMapping(GET_ELEMENT_TEXT, GetElementText::new);
    addNewMapping(SUBMIT_ELEMENT, SubmitElement::new);

    addNewMapping(UPLOAD_FILE, UploadFile::new);
    addNewMapping(SEND_KEYS_TO_ELEMENT, SendKeys::new);
    addNewMapping(GET_ELEMENT_TAG_NAME, GetTagName::new);

    addNewMapping(CLEAR_ELEMENT, ClearElement::new);
    addNewMapping(IS_ELEMENT_SELECTED, GetElementSelected::new);
    addNewMapping(IS_ELEMENT_ENABLED, GetElementEnabled::new);
    addNewMapping(IS_ELEMENT_DISPLAYED, GetElementDisplayed::new);
    addNewMapping(GET_ELEMENT_LOCATION, GetElementLocation::new);
    addNewMapping(GET_ELEMENT_LOCATION_ONCE_SCROLLED_INTO_VIEW, GetElementLocationInView::new);
    addNewMapping(GET_ELEMENT_SIZE, GetElementSize::new);
    addNewMapping(GET_ELEMENT_VALUE_OF_CSS_PROPERTY, GetCssProperty::new);
    addNewMapping(GET_ELEMENT_RECT, GetElementRect::new);

    addNewMapping(GET_ELEMENT_ATTRIBUTE, GetElementAttribute::new);
    addNewMapping(ELEMENT_EQUALS, ElementEquality::new);

    addNewMapping(GET_ALL_COOKIES, GetAllCookies::new);
    addNewMapping(GET_COOKIE, GetCookie::new);
    addNewMapping(ADD_COOKIE, AddCookie::new);
    addNewMapping(DELETE_ALL_COOKIES, DeleteCookie::new);
    addNewMapping(DELETE_COOKIE, DeleteNamedCookie::new);

    addNewMapping(SWITCH_TO_FRAME, SwitchToFrame::new);
    addNewMapping(SWITCH_TO_PARENT_FRAME, SwitchToParentFrame::new);
    addNewMapping(SWITCH_TO_WINDOW, SwitchToWindow::new);
    addNewMapping(CLOSE, CloseWindow::new);

    addNewMapping(GET_CURRENT_WINDOW_SIZE, GetWindowSize::new);
    addNewMapping(SET_CURRENT_WINDOW_SIZE, SetWindowSize::new);
    addNewMapping(GET_CURRENT_WINDOW_POSITION, GetWindowPosition::new);
    addNewMapping(SET_CURRENT_WINDOW_POSITION, SetWindowPosition::new);
    addNewMapping(MAXIMIZE_CURRENT_WINDOW, MaximizeWindow::new);
    addNewMapping(FULLSCREEN_CURRENT_WINDOW, FullscreenWindow::new);

    addNewMapping(SET_TIMEOUT, ConfigureTimeout::new);
    addNewMapping(IMPLICITLY_WAIT, ImplicitlyWait::new);
    addNewMapping(SET_SCRIPT_TIMEOUT, SetScriptTimeout::new);

    addNewMapping(GET_LOCAL_STORAGE_ITEM, GetLocalStorageItem::new);
    addNewMapping(REMOVE_LOCAL_STORAGE_ITEM, RemoveLocalStorageItem::new);
    addNewMapping(GET_LOCAL_STORAGE_KEYS, GetLocalStorageKeys::new);
    addNewMapping(SET_LOCAL_STORAGE_ITEM, SetLocalStorageItem::new);
    addNewMapping(CLEAR_LOCAL_STORAGE, ClearLocalStorage::new);
    addNewMapping(GET_LOCAL_STORAGE_SIZE, GetLocalStorageSize::new);

    addNewMapping(GET_SESSION_STORAGE_ITEM, GetSessionStorageItem::new);
    addNewMapping(REMOVE_SESSION_STORAGE_ITEM, RemoveSessionStorageItem::new);
    addNewMapping(GET_SESSION_STORAGE_KEYS, GetSessionStorageKeys::new);
    addNewMapping(SET_SESSION_STORAGE_ITEM, SetSessionStorageItem::new);
    addNewMapping(CLEAR_SESSION_STORAGE, ClearSessionStorage::new);
    addNewMapping(GET_SESSION_STORAGE_SIZE, GetSessionStorageSize::new);

    addNewMapping(MOVE_TO, MouseMoveToLocation::new);
    addNewMapping(CLICK, ClickInSession::new);
    addNewMapping(DOUBLE_CLICK, DoubleClickInSession::new);
    addNewMapping(MOUSE_DOWN, MouseDown::new);
    addNewMapping(MOUSE_UP, MouseUp::new);
    addNewMapping(SEND_KEYS_TO_ACTIVE_ELEMENT, SendKeyToActiveElement::new);

    addNewMapping(ACTIONS, W3CActions::new);

    // Advanced Touch API
    addNewMapping(TOUCH_SINGLE_TAP, SingleTapOnElement::new);
    addNewMapping(TOUCH_DOWN, Down::new);
    addNewMapping(TOUCH_UP, Up::new);
    addNewMapping(TOUCH_MOVE, Move::new);
    addNewMapping(TOUCH_SCROLL, Scroll::new);
    addNewMapping(TOUCH_DOUBLE_TAP, DoubleTapOnElement::new);
    addNewMapping(TOUCH_LONG_PRESS, LongPressOnElement::new);
    addNewMapping(TOUCH_FLICK, Flick::new);

    addNewMapping(GET_AVAILABLE_LOG_TYPES, GetAvailableLogTypesHandler::new);
    addNewMapping(GET_LOG, GetLogHandler::new);
    addNewMapping(GET_SESSION_LOGS, GetSessionLogsHandler::new);

    // Deprecated end points. Will be removed.
    addNewMapping("getWindowSize", GetWindowSize::new);
    addNewMapping("setWindowSize", SetWindowSize::new);
    addNewMapping("maximizeWindow", MaximizeWindow::new);
  }
}
