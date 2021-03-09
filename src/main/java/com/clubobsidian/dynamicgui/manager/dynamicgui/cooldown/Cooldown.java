/*
 *    Copyright 2021 Club Obsidian and contributors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.clubobsidian.dynamicgui.manager.dynamicgui.cooldown;

public class Cooldown {

	private String name;
	private Long time;
	private Long cooldown;
	public Cooldown(String name, Long time, Long cooldown)
	{
		this.name = name;
		this.time = time;
		this.cooldown = cooldown;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public Long getTime()
	{
		return this.time;
	}
	
	public Long getCooldown()
	{
		return this.cooldown;
	}
}