# encoding: UTF-8
# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended to check this file into your version control system.

ActiveRecord::Schema.define(:version => 20121204004018) do

  create_table "phone_registrations", :force => true do |t|
    t.string   "device_ident"
    t.string   "registration_ident"
    t.datetime "created_at",         :null => false
    t.datetime "updated_at",         :null => false
  end

  create_table "requests", :force => true do |t|
    t.string   "requester_device_ident"
    t.string   "intent_program"
    t.string   "output"
    t.string   "error"
    t.string   "status"
    t.integer  "num_phones"
    t.integer  "num_errors"
    t.datetime "created_at",             :null => false
    t.datetime "updated_at",             :null => false
  end

end
