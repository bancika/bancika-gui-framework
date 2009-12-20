package com.diyfever.gui;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.diyfever.gui.objecttable.ObjectListTableModelTest;
import com.diyfever.gui.simplemq.MessageDispatcherTest;

@RunWith(Suite.class)
@SuiteClasses( { MessageDispatcherTest.class, ObjectListTableModelTest.class })
public class AllTests {

}
