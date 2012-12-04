class CreatePhoneRegistrations < ActiveRecord::Migration
  def change
    create_table :phone_registrations do |t|
      t.string :device_ident
      t.string :registration_ident

      t.timestamps
    end
  end
end
